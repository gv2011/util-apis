package com.github.gv2011.util;

import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.bugValue;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.icol.ICollections.setBuilder;
import static com.github.gv2011.util.icol.ICollections.setFrom;
import static com.github.gv2011.util.icol.ICollections.setOf;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static com.github.gv2011.util.icol.ICollections.toISortedSet;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedSet;

public final class ReflectionUtils {

  private ReflectionUtils(){staticClass();}

  public static final Method EQUALS = call(()->Object.class.getMethod("equals", Object.class));
  public static final Method GET_CLASS = call(()->Object.class.getMethod("getClass"));
  public static final Method HASH_CODE = call(()->Object.class.getMethod("hashCode"));
  public static final Method NOTIFY = call(()->Object.class.getMethod("notify"));
  public static final Method NOTIFY_ALL = call(()->Object.class.getMethod("notifyAll"));
  public static final Method TO_STRING = call(()->Object.class.getMethod("toString"));
  public static final Method WAIT = call(()->Object.class.getMethod("wait"));
  public static final Method WAIT_LONG = call(()->Object.class.getMethod("wait", long.class));
  public static final Method WAIT_LONG_INT = call(()->Object.class.getMethod("wait", long.class, int.class));

  private static final Constant<ISet<Method>> OBJECT_METHODS = Constants.cachedConstant(()->setOf(
    EQUALS, GET_CLASS, HASH_CODE, NOTIFY, NOTIFY_ALL, TO_STRING, WAIT, WAIT_LONG,WAIT_LONG_INT
  ));

  private static final Constant<ISortedSet<String>> OBJECT_PROPERTY_METHOD_NAMES = Constants.cachedConstant(()->
      OBJECT_METHODS.get().stream()
      .filter(m->m.getParameterCount()==0)
      .map(Method::getName).collect(toISortedSet())
  );

  public static ISortedSet<String> objectPropertyNames(){
    return OBJECT_PROPERTY_METHOD_NAMES.get();
  }

  public static <T> Method method(final Class<T> intf, final Function<T,?> methodFunction){
    return methodLookup(intf).method(notNull(methodFunction));
  }

  public static <T> MethodSignature signature(final Class<T> intf, final Function<T,?> methodFunction){
    return new MethodSignature(method(intf, methodFunction));
  }

  public static <T> String methodName(final Class<T> intf, final Function<T,?> methodFunction){
    return method(intf, methodFunction).getName();
  }

  public static boolean inherits(final Method method, final Method superMethod){
    if(superMethod.getParameterCount()>0){throw new UnsupportedOperationException(
      format("Implemented only for methods without parameters ({}).", superMethod)
    );}
    if(method.getParameterCount()>0) return false;
    else if(!superMethod.getName().equals(method.getName())) return false;
    else return superMethod.getDeclaringClass().isAssignableFrom(method.getDeclaringClass());
  }

  public static final class Lookup<T>{
    private final T proxy;
    private final ThreadLocal<Method> method = new ThreadLocal<>();
    private Lookup(final Class<T> interfaze){
      final InvocationHandler ih = (proxy, method, args) -> {
        notNull(method);
        this.method.set(method);
        return defaultValue(method.getReturnType());
      };
      proxy = createProxy(interfaze, ih);
    }
    public Method method(final Function<T,?> methodFunction){
      methodFunction.apply(proxy);
      final Method result = notNull(method.get());
      return result;
    }
  }

  public static final @Nullable Object defaultValue(final Class<?> clazz) {
    if(clazz.isPrimitive()) {
      if(clazz==boolean.class) return false;
      else if(clazz==byte.class) return (byte)0;
      else if(clazz==short.class) return (short)0;
      else if(clazz==int.class) return (int)0;
      else if(clazz==long.class) return (long)0;
      else if(clazz==float.class) return (float)0;
      else if(clazz==double.class) return (double)0;
      else if(clazz==void.class) return null;
      else return bugValue();
    }
    else return null;
  }


  public static <T> Lookup<T> methodLookup(final Class<T> intf){
    return new Lookup<>(intf);
  }

  public static <T> T createProxy(final Class<T> intf, final InvocationHandler ih) {
    return intf.cast(Proxy.newProxyInstance(intf.getClassLoader(), new Class<?>[]{intf}, ih));
  }

  public static Method getOwnMethod(final Object target, final Method method, final Object [] parameters){
    try {
      final Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
      return targetMethod;
    }
    catch (IllegalArgumentException | NoSuchMethodException | SecurityException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> Method attributeMethod(final Class<T> intf, final Function<T,?> methodFunction){
    final Method method = method(intf, methodFunction);
    final Object error = checkIsAttributeMethod(method);
    if(error!=null) throw new RuntimeException(error.toString());
    return method;
  }

  private static Object checkIsAttributeMethod(final Method method) {
    if(method.getParameterCount()!=0) return 1;
    return null;
  }

  public static ISet<Class<?>> getAllInterfaces(final Class<?> clazz){
    final Set<Type> types = new HashSet<>();
    collectAllInterfaces(types, clazz);
    return types.stream()
      .flatMap(t->{
        if(t instanceof Class) return Stream.of((Class<?>)t);
        else if(t instanceof ParameterizedType) return Stream.of((Class<?>)((ParameterizedType)t).getRawType());
        else return Stream.<Class<?>>empty();
      })
      .collect(toISet())
    ;
  }

  public static ISet<Type> getAllPInterfaces(final Class<?> clazz){
    final Set<Type> result = new HashSet<>();
    collectAllInterfaces(result, clazz);
    return setFrom(result);
  }

  private static void collectAllInterfaces(final Set<Type> result, final Type type){
    Optional<Class<?>> clazz;
    if(type instanceof ParameterizedType){
      clazz = Optional.of((Class<?>)((ParameterizedType)type).getRawType());
    }
    else if(type instanceof Class){
      clazz = Optional.of((Class<?>)type);
    }
    else clazz = Optional.empty();
    if(clazz.isPresent()){
      final @Nullable Type superclass = clazz.get().getGenericSuperclass();
      if(superclass!=null){
        collectAllInterfaces(result, superclass);
      }
      final Set<Type> superInterfaces = Arrays.stream(clazz.get().getGenericInterfaces())
        .collect(toSet())
      ;
      for(final Type i: superInterfaces){
        final boolean added = result.add(i);
        if(added) collectAllInterfaces(result, i);
      }
    }
  }

  public static ISortedSet<String> toStrings(final ISet<Method> methods){
    final ISet.Builder<Class<?>> classes = setBuilder();
    for(final Method m: methods){
      classes.tryAdd(m.getDeclaringClass());
      classes.tryAdd(m.getReturnType());
      for(final Class<?> c: m.getParameterTypes()) classes.tryAdd(c);
    }
    final Function<Class<?>, String> nameShortener = nameShortener(classes.build());
    final ISortedSet<String> result = methods.stream()
      .map(m->
        m.getName() +
        XStream.of(m.getParameterTypes()).map(nameShortener).collect(joining(",","(",")")) +
        ":"+nameShortener.apply(m.getDeclaringClass())+"->"+nameShortener.apply(m.getReturnType())
      )
      .collect(toISortedSet())
    ;
    verify(result.size()==methods.size());
    return result;
  }

  public static Function<Class<?>,String> nameShortener(final ISet<Class<?>> classes){
    final ISet<Class<?>> notUnique;
    {
      final Set<String> simpleNames = new HashSet<>();
      final ISet.Builder<Class<?>> notUniqueB = setBuilder();
      for(final Class<?> c: classes){
        if(!simpleNames.add(c.getSimpleName())) notUniqueB.add(c);
      }
      notUnique = notUniqueB.build();
    }
    return c->notUnique.contains(c)?c.getName():c.getSimpleName();
  }

  public static Class<?> getWrapperClass(final Class<?> clazz) {
    if(clazz.isPrimitive()) {
      if(clazz==boolean.class) return Boolean.class;
      else if(clazz==byte.class) return Byte.class;
      else if(clazz==short.class) return Short.class;
      else if(clazz==int.class) return Integer.class;
      else if(clazz==long.class) return Long.class;
      else if(clazz==float.class) return Float.class;
      else if(clazz==double.class) return Double.class;
      else if(clazz==void.class) return Void.class;
      else return bugValue();
    }
    else return clazz;
  }

}
