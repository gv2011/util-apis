package com.github.gv2011.util.icol;

import static com.github.gv2011.util.icol.ICollections.setFrom;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


public class IEmpty<E> implements Opt<E>{

  @SuppressWarnings("rawtypes")
  public static final Opt INSTANCE = Nothing.INSTANCE;

  @SuppressWarnings("rawtypes")
  static final ListIterator EMPTY_ITERATOR = new EmptyIterator();

  protected IEmpty(){}

  @Override
  public int size() {
    return 0;
  }

  @Override
  public boolean contains(final Object o) {
    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator<E> iterator() {
    return EMPTY_ITERATOR;
  }

  @Override
  public Object[] toArray() {
    return new Object[0];
  }

  @Override
  public String toString() {
    return "[]";
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean equals(final Object obj) {
    if(this==obj) return true;
    else if(!(obj instanceof Set)) return false;
    else return ((Set<?>) obj).isEmpty();
  }

  @Override
  public boolean containsAll(final Collection<?> c) {
    return c.isEmpty();
  }

  @Override
  public E get() {
    throw new NoSuchElementException();
  }

  @Override
  public boolean isPresent() {
    return false;
  }

  @Override
  public IEmpty<E> filter(final Predicate<? super E> predicate) {
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <U> Opt<U> map(final Function<? super E, ? extends U> mapper) {
    return (Opt<U>) this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <U> Opt<U> flatMap(final Function<? super E, ? extends Opt<? extends U>> mapper) {
    return (Opt<U>) this;
  }

  @Override
  public E orElse(final E other) {
    return other;
  }

  @Override
  public E orElseGet(final Supplier<? extends E> supplier) {
    return supplier.get();
  }

  @Override
  public <X extends Throwable> E orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X {
    throw exceptionSupplier.get();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Opt<E> or(final Supplier<? extends Opt<? extends E>> supplier) {
    return (Opt<E>) supplier.get();
  }


  @Override
  public ISet<E> join(final Collection<? extends E> other) {
    return setFrom(other);
  }

  @Override
  public IEmpty<E> subtract(final Collection<?> other) {
    return this;
  }


  @Override
  public Opt<E> addElement(final E element) {
    return ICollections.single(element);
  }

  @Override
  public <T> T[] toArray(final T[] a) {
    if(a.length>0) a[0] = null;
    return a;
  }


  private static final class EmptyIterator<T> implements ListIterator<T>{
    @Override
    public boolean hasNext() {
      return false;
    }
    @Override
    public T next() {
      throw new NoSuchElementException();
    }
    @Override
    public boolean hasPrevious() {
      return false;
    }
    @Override
    public T previous() {
      throw new NoSuchElementException();
    }
    @Override
    public int nextIndex() {
      return 0;
    }
    @Override
    public int previousIndex() {
      return -1;
    }
    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
    @Override
    public void set(final T e) {
      throw new UnsupportedOperationException();
    }
    @Override
    public void add(final T e) {
      throw new UnsupportedOperationException();
    }
  }


  @Override
  public IEmpty<E> intersection(Collection<?> other) {
    return this;
  }

}
