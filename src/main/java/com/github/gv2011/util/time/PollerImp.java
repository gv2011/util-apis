package com.github.gv2011.util.time;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;

import com.github.gv2011.util.ex.ThrowingSupplier;
import com.github.gv2011.util.icol.Opt;

final class PollerImp implements Poller{
  
  private static final Logger LOG = getLogger(PollerImp.class);
  
  private final Clock clock;
  private final Duration interval;
  private final Opt<Duration> timeout;
  
  private final Object lock = new Object();
  
  private boolean closed;
  
  PollerImp(Clock clock, Duration interval, Opt<Duration> timeout) {
    this.clock = clock;
    this.interval = interval;
    this.timeout = timeout;
  }


  @Override
  public <T> Opt<T> poll(ThrowingSupplier<Opt<T>> operation) {
    synchronized(lock){verify(!closed);}
    Opt<Instant> limit = timeout.map(t->clock.instant().plus(t));
    Opt<T> result = Opt.empty();
    while(!closed && result.isEmpty() && limit.map(l->!clock.hasPassed(l)).orElse(true)){
      result = call(operation);
      if(result.isEmpty()){
        synchronized(lock){
          Instant next = clock.instant().plus(interval);
          if(limit.isPresent()){
            next = TimeUtils.earliest(next, limit.get());
          }
          clock.notifyAt(lock, next);
          try {
            lock.wait();
          } catch (InterruptedException e) {
            closed = true;
            LOG.warn("Closed by interrupt.", e);
          }
        }
      }
    }
    return result;
  }
  
  @Override
  public void close() {
    synchronized(lock){
      closed = true;
      lock.notifyAll();
    }
  }


}
