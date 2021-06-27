package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.util.Arrays;
import java.util.function.Function;

import com.github.gv2011.util.beans.BeanBuilder;
import com.github.gv2011.util.beans.BeanHashCode;
import com.github.gv2011.util.beans.TypeRegistry;
import com.github.gv2011.util.json.JsonObject;

public final class BeanUtils {

  private static final Constant<TypeRegistry> TYPE_REGISTRY = ServiceLoaderUtils.lazyServiceLoader(TypeRegistry.class);

  private BeanUtils(){staticClass();}

  public static TypeRegistry typeRegistry(){
      return TYPE_REGISTRY.get();
  }

  public static <B> BeanBuilder<B> beanBuilder(final Class<B> beanClass){
      return typeRegistry().beanType(beanClass).createBuilder();
  }

  public static <B> B parse(final Class<B> beanClass, final JsonObject json) {
      return typeRegistry().beanType(beanClass).parse(json);
  }

  public static <B> B parse(final Class<B> beanClass, final String string) {
    return typeRegistry().beanType(beanClass).parse(string);
  }

  /**
   * @see BeanHashCode
   */
  @SafeVarargs
  public static <B> boolean equals(final B obj, final Object other, final Class<B> clazz, final Function<B,?>... attributes){
    if(obj==other) return true;
    else if(!clazz.isInstance(other)) return false;
    else{
      final B o = clazz.cast(other);
      return Arrays.stream(attributes).allMatch(a->a.apply(obj).equals(a.apply(o)));
    }
  }

}
