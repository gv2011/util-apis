package com.github.gv2011.util.jdbc;

import static com.github.gv2011.util.Verify.verify;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

public interface DbSet<B> extends Collection<B>{

  long longSize();

  @Override
  default int size() {
    final long size = longSize();
    verify(size, s->s<=Integer.MAX_VALUE);
    return (int)size;
  }

  @Override
  default Iterator<B> iterator() {
    return stream().iterator();
  }

  @Override
  default Stream<B> parallelStream() {
    return stream().parallel();
  }

}
