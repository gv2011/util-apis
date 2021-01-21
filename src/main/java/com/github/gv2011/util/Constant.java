package com.github.gv2011.util;

import java.util.function.Supplier;


@com.github.gv2011.util.ann.Immutable
@FunctionalInterface
public interface Constant<T> extends Supplier<T>{
	
  public static <T> Constant<T> of(final T t){return ()->t;}

  /**
   * @return always the same value
   */
  @Override
  T get();

}
