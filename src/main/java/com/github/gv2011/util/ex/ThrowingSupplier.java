package com.github.gv2011.util.ex;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;
import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowingSupplier<R> extends ArgumentIgnoringThrowingFunction<Object,R>{

  R getThrowing() throws Exception;

  default R get() {
    try {
       return getThrowing();
    }
    catch (final InterruptedException e) {
      throw new InterruptedRtException(e);
    }
    catch (final InterruptedIOException e) {
      throw new InterruptedRtException(e);
    }
    catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
    catch (final RuntimeException e) {
      throw e;
    }
    catch (final Exception e) {
      throw new WrappedException(e);
    }
  }

  @Override
  default Function<Object,R> asFunction() {
    return o->get();
  }

  default Function<Object,R> asFunction(final Supplier<?> exceptionMessage) {
    return (final Object arg) -> {
      try {
         return getThrowing();
      }
      catch (final Exception e) {
        throw new WrappedException(e, String.valueOf(exceptionMessage.get()));
      }
    };
  }

}
