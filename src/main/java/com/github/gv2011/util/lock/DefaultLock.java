package com.github.gv2011.util.lock;

import static org.slf4j.LoggerFactory.getLogger;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.github.gv2011.util.ex.Exceptions;
import com.github.gv2011.util.time.Clock;

class DefaultLock implements Lock{
  
  private static final Logger LOG = getLogger(DefaultLock.class);

  private final Object internalLock = new Object();

  @Override
  public final void run(final Runnable operation, final boolean notify) {
    assert !Thread.holdsLock(this);
    synchronized(internalLock){
      operation.run();
      if(notify) internalLock.notifyAll();
    }
  }

  @Override
  public final <T> T get(final Supplier<T> operation) {
    assert !Thread.holdsLock(this);
    synchronized(internalLock){
      return operation.get();
    }
  }

  @Override
  public final <A, R> R apply(final A argument, final Function<A, R> operation) {
    assert !Thread.holdsLock(this);
    synchronized(internalLock){
      return operation.apply(argument);
    }
  }

  @Override
  public final boolean isLocked() {
    assert !Thread.holdsLock(this);
    return Thread.holdsLock(internalLock);
  }

  @Override
  public final void publish() {
    assert !Thread.holdsLock(this);
    synchronized(internalLock){
      internalLock.notifyAll();
    }
  }

  @Override
  public final void await() {
    assert !Thread.holdsLock(this);
    synchronized(internalLock){
      Exceptions.call(()->internalLock.wait());
    }
  }

  @Override
  public final void await(final Duration timeOut) {
    assert !Thread.holdsLock(this);
    getClock().notifyAfter(internalLock, timeOut);
    synchronized(internalLock){
      Exceptions.call(()->internalLock.wait());
    }
  }

  @Override
  public <R> R callWhen(Supplier<Boolean> condition, Supplier<R> operation) {
    assert !Thread.holdsLock(this);
    synchronized(internalLock){
      boolean first = true;
      while(!condition.get()){
        if(!first){LOG.warn("{} waits for {}.", Thread.currentThread(), condition);}
        else first = false;
        getClock().notifyAfter(internalLock, Duration.ofSeconds(8));
        Exceptions.call(()->internalLock.wait());
      }
      return operation.get();
    }
  }

  Clock getClock(){
    return Clock.INSTANCE.get();
  }

}
