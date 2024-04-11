package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.icol.ICollections.toISortedSet;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import com.github.gv2011.util.icol.ISortedSet;

public final class EnumUtils {

  private EnumUtils(){staticClass();}

  private static final Map<Class<? extends Enum<?>>, ISortedSet<? extends Enum<?>>> CACHE =
    Collections.synchronizedMap(new WeakHashMap<>())
  ;

  private static final Map<Class<? extends Enum<?>>, ISortedSet<String>> CACHE_STR =
    Collections.synchronizedMap(new WeakHashMap<>())
  ;

  @SuppressWarnings("unchecked")
  public static <E extends Enum<E>> ISortedSet<E> values(final Class<E> enumClass){
    return (ISortedSet<E>) CACHE.computeIfAbsent(
      enumClass,
      c->CollectionUtils.stream(enumClass.getEnumConstants()).collect(toISortedSet())
    );
  }

  public static <E extends Enum<E>> ISortedSet<String> stringValues(final Class<E> enumClass){
    return CACHE_STR.computeIfAbsent(
      enumClass,
      c->values(enumClass).stream().map(Object::toString).collect(toISortedSet())
    );
  }

  public static <E extends Enum<E>> XStream<E> stream(final Class<E> enumClass){
    return values(enumClass).stream();
  }


}
