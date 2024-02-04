package com.github.gv2011.util.lock;

import static com.github.gv2011.util.icol.ICollections.nothing;

import java.time.Duration;
import java.util.function.Supplier;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.ex.ThrowingFunction;
import com.github.gv2011.util.ex.ThrowingRunnable;
import com.github.gv2011.util.ex.ThrowingSupplier;
import com.github.gv2011.util.icol.Nothing;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;
import com.github.gv2011.util.time.Clock;

public interface Lock {

  public static final Constant<Factory> FACTORY = RecursiveServiceLoader.lazyService(Factory.class);

  public static interface Factory{
    default Lock create(){
      return create(Clock.INSTANCE.get());
    }
    Lock create(Clock clock);
    <T> Latch<T> createLatch();
  }

  public static Lock create(){
    return FACTORY.get().create();
  }

  default void run(final Runnable operation){
    run(operation, false);
  }

  void run(Runnable operation, boolean notify);

  boolean isLocked();

  void publish();

  <T> T get(Supplier<T> operation);

  void await();

  void await(final Duration timeOut);

  <A,R> R apply(A argument, ThrowingFunction<A,R> operation);

  <R> R callWhen(Supplier<Boolean> condition, Supplier<R> operation);

  default <R> R call(final ThrowingSupplier<R> operation) {
    return apply(nothing(), n->operation.get());
  }

  default Nothing call(final ThrowingRunnable operation) {
    return apply(nothing(), n->{operation.run(); return nothing();});
  }

}
