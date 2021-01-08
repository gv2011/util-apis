package com.github.gv2011.util.ex;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T,R> extends Throwing<T,R>{

  R apply(T argument) throws Exception;

  @Override
  default Function<T,R> asFunction() {
    return (final T arg) -> {
      try {
         return apply(arg);
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
    };
  }


}
