package com.github.gv2011.util.time;

import static com.github.gv2011.util.Verify.verify;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;

import static com.github.gv2011.util.time.TimeUtils.isPositive;
import static org.slf4j.LoggerFactory.getLogger;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.ex.ThrowingRunnable;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.time.DefaultClock.Inline;

final class ClockTask implements AutoCloseableNt, Inline{

  private static final Logger LOG = getLogger(ClockTask.class);

  private final Clock clock;
  private final ThrowingRunnable operation;
  private final Opt<Duration> interval;

  private final Object lock = new Object();

  private boolean closed;

  private Instant nextTime;

  ClockTask(final Clock clock, final ThrowingRunnable operation, final Duration interval) {
    verify(isPositive(interval));
    this.clock = clock;
    this.operation = operation;
    this.interval = Opt.of(interval);
    nextTime = clock.instant().plus(interval);
    clock.notifyAt(this, nextTime);
  }

  ClockTask(final Clock clock, final ThrowingRunnable operation, final Instant time) {
    this.clock = clock;
    this.operation = operation;
    this.interval = Opt.empty();
    nextTime = time;
    clock.notifyAt(this, nextTime);
  }

  @Override
  public void close() {
    synchronized(lock){closed = true;}
  }

  @Override
  public void run() {
    synchronized(lock){
      if(!closed){
        final Instant now = clock.instant();
        if(now.isBefore(nextTime)) clock.notifyAt(this, nextTime);
        else{
          if(interval.isPresent()){
            nextTime = nextTime.plus(interval.get());
            clock.notifyAt(this, nextTime);
          }
          else{
            closed = true;
          }
          new Thread(()->{
              try {
                operation.runThrowing();
              } catch (final Throwable e) {
                LOG.error("Error, task will not be executed again.", e);
                close();
              }
            })
            .start()
          ;
        }
      }
    }
  }

}
