package com.github.gv2011.util.sec;

import javax.security.auth.Destroyable;

import com.github.gv2011.util.AutoCloseableNt;

public interface DestroyingCloseable extends Destroyable, AutoCloseableNt{

  @Override
  default boolean closed() {
    return isDestroyed();
  }

  @Override
  default void close() {
    destroy();
  }

  @Override
  void destroy();

}
