package com.github.gv2011.util.icol;

import static com.github.gv2011.util.Verify.verify;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Ref<E> implements Opt<E>{

  @Override
  public final int size() {
    return 1;
  }

  @Override
  public final boolean contains(final Object o) {
    return get().equals(o);
  }

  @Override
  public final Iterator<E> iterator() {
    return new It();
  }

  @Override
  public final Object[] toArray() {
    return new Object[]{get()};
  }

  @Override
  public final boolean containsAll(final Collection<?> c) {
    return c.parallelStream().allMatch(e->e.equals(get()));
  }

  @Override
  public final boolean isPresent() {
    return true;
  }

  @SuppressWarnings("unchecked")
  @Override
  public final Opt<E> filter(final Predicate<? super E> predicate) {
    return predicate.test(get()) ? this : ICollections.EMPTY;
  }

  @SuppressWarnings("unchecked")
  @Override
  public final <U> Opt<U> flatMap(final Function<? super E, ? extends Opt<? extends U>> mapper) {
    return (Opt<U>) mapper.apply(get());
  }

  @Override
  public final Ref<E> or(final Supplier<? extends Opt<? extends E>> supplier) {
    return this;
  }

  @Override
  public final E orElse(final E other) {
    return get();
  }

  @Override
  public final E orElseGet(final Supplier<? extends E> supplier) {
    return get();
  }

  @Override
  public final <X extends Throwable> E orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X {
    return get();
  }

  @Override
  public final int hashCode() {
    return get().hashCode();
  }

  @Override
  public final boolean equals(final Object obj) {
    if(this==obj) return true;
    else if(obj instanceof Opt){
      final Opt<?> opt = ((Opt<?>) obj);
      if(opt.isPresent()) return get().equals(opt.get());
      else return false;
    }
    else if(!(obj instanceof Set)) return false;
    else{
      final Set<?> other = (Set<?>) obj;
      if(other.size()!=1) return false;
      else return get().equals(other.iterator().next());
    }
  }

  @Override
  public final String toString() {
    return "["+get()+"]";
  }

  private final class It implements ListIterator<E> {
    private boolean done;
    @Override
    public boolean hasNext() {return !done;}
    @Override
    public E next() {
      verify(!done);
      done = true;
      return get();
    }
    @Override
    public boolean hasPrevious() {
      return done;
    }
    @Override
    public E previous() {
      verify(done);
      done = false;
      return get();
    }
    @Override
    public int nextIndex() {
      return done ? 1 : 0;
    }
    @Override
    public int previousIndex() {
      return done ? 0 : -1;
    }
    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
    @Override
    public void set(final E e) {
      throw new UnsupportedOperationException();
    }
    @Override
    public void add(final E e) {
      throw new UnsupportedOperationException();
    }
  }


}
