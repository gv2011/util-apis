package com.github.gv2011.util.ex;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;
import java.util.function.Function;

import com.github.gv2011.util.Nothing;

@FunctionalInterface
public interface ThrowingRunnable extends ArgumentIgnoringThrowingFunction<Object,Nothing>{

  void run() throws Exception;

  @Override
  default Function<Object,Nothing> asFunction() {
    return (final Object arg) -> {
      try {
         run();
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
      return Nothing.INSTANCE;
    };
  }


}
