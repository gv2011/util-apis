package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import com.github.gv2011.util.ex.ThrowingSupplier;

public final class ConcurrencyUtils {

  private ConcurrencyUtils(){staticClass();}

  public static <T> T safePublish(final ThrowingSupplier<? extends T> constructor){
    return new Holder<>(constructor).get();
  }

  private static final class Holder<T>{
    @SuppressWarnings("unused")
    private final Class<?> clazz;
    private final T obj;
    private Holder(final ThrowingSupplier<? extends T> constructor){
      final T obj = constructor.get();
      this.clazz = obj.getClass();
      this.obj = obj;
    }
    private T get(){
      return obj;
    }
  }

}
