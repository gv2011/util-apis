package com.github.gv2011.util.beans;

import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import com.github.gv2011.util.ReflectionUtils;

public final class BeanHashCode {

  private BeanHashCode(){staticClass();}

  @SafeVarargs
  public static <B> ToIntFunction<B> createHashCodeFunction(
    final Class<B> beanInterface, final Function<B,?>... attributes
  ){
    return createHashCodeFunction(beanInterface, Arrays.asList(attributes));
  }

  public static <B> ToIntFunction<B> createHashCodeFunction(
      final Class<B> beanInterface, final Collection<Function<B,?>> attributes
    ){
    return createHashCodeFunctionNamed(
      beanInterface,
      ( attributes.stream()
        .collect(Collectors.toMap(
          a->ReflectionUtils.methodName(beanInterface, a),
          a->a
        ))
      )
    );
  }

  public static <B> ToIntFunction<B> createHashCodeFunctionNamed(
    final Class<B> beanClass, final Map<String, Function<B,?>> attributes
  ){
    final int base = classHashCode(beanClass) * 31;
    final List<ToIntFunction<B>> attributeFunctions = Collections.unmodifiableList(
      attributes.entrySet().stream()
      .map(a->{
        final int attributeNameHash = a.getKey().hashCode();
        final Function<B,?> attributeValueFunction = a.getValue();
        return (ToIntFunction<B>) b->attributeNameHash ^ attributeValueFunction.apply(b).hashCode();
      })
      .collect(toList())
    );
    return b->base + attributeFunctions.parallelStream().mapToInt(af->af.applyAsInt(b)).sum();
  }

  public static int classHashCode(final Class<?> clazz) {
    return clazz.getName().hashCode();
  }

}
