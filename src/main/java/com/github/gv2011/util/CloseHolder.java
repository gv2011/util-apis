package com.github.gv2011.util;

import static com.github.gv2011.util.Verify.verify;

import com.github.gv2011.util.ex.ThrowingRunnable;
import com.github.gv2011.util.icol.Ref;

public final class CloseHolder<T> extends Ref<T> implements AutoCloseableNt{

  public static <T extends AutoCloseable> CloseHolder<T> wrap(final T element){
    return wrap(element, ()->element.close());
  }

  public static <T> CloseHolder<T> dontClose(final T element){
    return wrap(element, ()->{});
  }

  public static <T> CloseHolder<T> wrap(final T element, final ThrowingRunnable closeOperation){
    return new CloseHolder<T>(element, closeOperation);
  }

  private final Object lock = new Object();
  private final ThrowingRunnable closeOperation;
  private final T element;
  private boolean closed = false;

  private CloseHolder(final T element, final ThrowingRunnable closeOperation) {
    this.element = element;
    this.closeOperation = closeOperation;
  }

  @Override
  public T get() {
    synchronized(lock){
      verify(!closed);
      return element;
    }
  }

  @Override
  public void close() {
    synchronized(lock){
      if(!closed){
        closed = true;
        closeOperation.run();
      }
    }
  }

}
