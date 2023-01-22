package com.github.gv2011.util.ex;

import static com.github.gv2011.util.icol.ICollections.nothing;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;
import java.util.function.Function;

import com.github.gv2011.util.icol.Nothing;


@FunctionalInterface
public interface ThrowingConsumer<T> extends Throwing<T,Nothing>{

  void accept(T arg) throws Exception;

  default Consumer<T> andThen(final ThrowingConsumer<? super T> next) {
    return (final T arg) -> {
      asFunction().apply(arg);
      next.asFunction().apply(arg);
    };
  }

  @Override
  default Function<T,Nothing> asFunction() {
    return (final T arg) -> {
      try {
        accept(arg);
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
      return nothing();
    };
  }

}
