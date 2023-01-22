package com.github.gv2011.util.time;

import static com.github.gv2011.util.Verify.verify;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;

import static com.github.gv2011.util.time.TimeUtils.isPositive;
import static org.slf4j.LoggerFactory.getLogger;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.ex.ThrowingRunnable;
import com.github.gv2011.util.time.DefaultClock.Inline;

final class PeriodicalTask implements AutoCloseableNt, Inline{
  
  private static final Logger LOG = getLogger(PeriodicalTask.class);
  
  private final Clock clock;
  private final ThrowingRunnable operation;
  private final Duration interval;

  private final Object lock = new Object();
  
  private boolean closed;

  private Instant nextTime;

  PeriodicalTask(Clock clock, ThrowingRunnable operation, Duration interval) {
    verify(isPositive(interval));
    this.clock = clock;
    this.operation = operation;
    this.interval = interval;
    nextTime = clock.instant().plus(interval);
    clock.notifyAt(this, nextTime);
  }

  @Override
  public void close() {
    synchronized(lock){this.closed = true;}
  }

  @Override
  public void run() {
    synchronized(lock){
      if(!closed){
        nextTime = nextTime.plus(interval);
        new Thread(()->{
            try {
              operation.runThrowing();
            } catch (Throwable e) {
              LOG.error("Error, task will not be executed again.", e);
              close();
            }
          })
          .start()
        ;
        clock.notifyAt(this, nextTime);
      }
    }
  }

}
