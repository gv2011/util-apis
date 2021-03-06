package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.util.Arrays;
import java.util.function.Function;

public final class Equal {

  private Equal(){staticClass();}

  public static final boolean equal(final Object o1, final Object o2){
    boolean result;
    if(o1==o2) result = true;
    else if(o1==null || o2==null) result = false;
    else {
      result = o1.equals(o2);
      assert result==o2.equals(o1) : "Inconsistent equals implementation.";
    }
    return result;
  }

  public static <T> boolean equal(final T o1, final Object o2, final Class<T> clazz, final Function<T,Boolean> eq){
    if(o1==o2) return true;
    else if(!clazz.isInstance(o2)) return false;
    else return eq.apply(clazz.cast(o2));
  }

  public static <T extends Comparable<? super T>> boolean equal(final T o1, final Object o2, final Class<T> clazz){
    if(o1==o2) return true;
    else if(!clazz.isInstance(o2)) return false;
    else return o1.compareTo(clazz.cast(o2))==0;
  }

  public static int hashCode(final Class<?> clazz, final Object att1, final Object... more){
    int hash = (clazz.hashCode()*31)+att1.hashCode();
    for(final Object o: more) hash = hash*31 + o.hashCode();
    return hash;
  }

  @SafeVarargs
  public static <T> boolean calcEqual(
    final T o1, final Object o2, final Class<T> clazz, final Function<T,?>... attributes
  ){
    if(o1==o2) return true;
    else if(!clazz.isInstance(o2)) return false;
    else{
      final T o2c = clazz.cast(o2);
      return Arrays.stream(attributes).allMatch(a->a.apply(o1).equals(a.apply(o2c)));
    }
  }

}
