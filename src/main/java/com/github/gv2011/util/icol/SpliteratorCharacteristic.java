package com.github.gv2011.util.icol;

import static com.github.gv2011.util.icol.ICollections.toISortedSet;
import static java.util.Comparator.comparing;

import java.util.Arrays;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;

public enum SpliteratorCharacteristic {

  CONCURRENT(Spliterator.CONCURRENT),
  DISTINCT(Spliterator.DISTINCT),
  IMMUTABLE(Spliterator.IMMUTABLE),
  NONNULL(Spliterator.NONNULL),
  ORDERED(Spliterator.ORDERED),
  SIZED(Spliterator.SIZED),
  SORTED(Spliterator.SORTED),
  SUBSIZED(Spliterator.SUBSIZED)
  ;

  private final int code;

  private SpliteratorCharacteristic(final int code){
    this.code = code;
  }

  public int code(){return code;}

  public static ISortedSet<SpliteratorCharacteristic> characteristics(final Spliterator<?> s){
    final AtomicInteger c = new AtomicInteger(s.characteristics());
    return Arrays.stream(SpliteratorCharacteristic.values())
      .sorted(comparing(SpliteratorCharacteristic::code).reversed())
      .sequential()
      .filter(sc->{
        if(c.get()>=sc.code()) {c.addAndGet(-sc.code()); return true;}
        else return false;
      })
      .collect(toISortedSet())
    ;
  }

}
