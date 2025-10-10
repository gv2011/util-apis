package com.github.gv2011.util.time;

import java.time.Duration;

import java.time.Instant;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.Constant;
import com.github.gv2011.util.ex.ThrowingRunnable;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;
import com.github.gv2011.util.serviceloader.Service;

@Service(defaultImplementation="com.github.gv2011.util/com.github.gv2011.util.time.DefaultClock")
public interface Clock {

  public static final Constant<Clock> INSTANCE = RecursiveServiceLoader.lazyService(Clock.class);

  public static Clock get(){
    return INSTANCE.get();
  }

  Instant instant();

  void await(Instant nextRun);

  void sleep(Duration sleepTime);

  void notifyAfter(Object obj, Duration duration);

  void notifyAt(final Object obj, final Instant notifyAt);

  default boolean hasPassed(final Instant time){
    return instant().isAfter(time);
  }

  AutoCloseableNt runAtInterval(ThrowingRunnable operation, Duration interval);

  AutoCloseableNt runAt(ThrowingRunnable operation, Instant time);

  Poller poller(Duration interval, Opt<Duration> timeout);
}
