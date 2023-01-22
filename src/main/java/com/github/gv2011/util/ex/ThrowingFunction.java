package com.github.gv2011.util.ex;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T,R> extends Throwing<T,R>{

  R applyThrowing(T argument) throws Exception;

  default R apply(final T argument) {
    try {
      return applyThrowing(argument);
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
  default Function<T,R> asFunction() {
    return a->apply(a);
  }

}
