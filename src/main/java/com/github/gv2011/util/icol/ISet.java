package com.github.gv2011.util.icol;

import static com.github.gv2011.util.icol.ICollections.toISet;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import com.github.gv2011.util.XStream;

public interface ISet<E> extends Set<E>, ICollectionG<E, ISet<E>>{

  public static interface Builder<E> extends CollectionBuilder<ISet<E>,E,Builder<E>>{}

  @SuppressWarnings("unchecked")
  static <E> ISet<E> cast(final ISet<? extends E> set){return (ISet<E>) set;}

  @Override
  default boolean isEmpty() {
    return size()==0;
  }

  @Override
  default Object[] toArray() {
    return asList().toArray();
  }

  @Override
  default <T> T[] toArray(final T[] a) {
    return asList().toArray(a);
  }

  @Override
  default boolean containsAll(final Collection<?> c) {
    return c.stream().allMatch(this::contains);
  }

  @Override
  default ISet<E> subtract(final Collection<?> other) {
    if(other.isEmpty()) return this;
    else{
      return parallelStream().filter(e->!other.contains(e)).collect(toISet());
    }
  }

  @Override
  default XStream<E> stream() {
      return XStream.stream(spliterator(), false).unordered();
  }

  @Override
  default XStream<E> parallelStream() {
      return XStream.stream(spliterator(), true).unordered();
  }

  @Override
  default ISet<E> join(final Collection<? extends E> other) {
    return parallelStream().concat(other.parallelStream().unordered()).collect(toISet());
  }

  @Deprecated
  @Override
  default boolean add(final E e) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default boolean remove(final Object o) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default boolean addAll(final Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default boolean removeAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default boolean removeIf(final Predicate<? super E> filter) {
    throw new UnsupportedOperationException();
  }


  @Deprecated
  @Override
  default boolean retainAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default void clear() {
    throw new UnsupportedOperationException();
  }
}
