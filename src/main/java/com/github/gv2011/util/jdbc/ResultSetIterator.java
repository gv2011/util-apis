package com.github.gv2011.util.jdbc;

import static com.github.gv2011.util.ex.Exceptions.call;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.github.gv2011.util.ex.ThrowingFunction;

final class ResultSetIterator<T> implements Iterator<T>{

  private final ResultSet rs;
  private boolean hasNext;
  private final ThrowingFunction<ResultSet, T> extractor;

  ResultSetIterator(final ResultSet rs, final ThrowingFunction<ResultSet, T> extractor){
    this.rs = rs;
    this.extractor = extractor;
    getNext();
  }

  private void getNext() {
    hasNext = call(rs::next);
    if(!hasNext) call(rs::close);
  }

  @Override
  public boolean hasNext() {
    return hasNext;
  }

  @Override
  public T next() {
    if(!hasNext) throw new NoSuchElementException();
    final T result = extractor.apply(rs);
    getNext();
    return result;
  }

}
