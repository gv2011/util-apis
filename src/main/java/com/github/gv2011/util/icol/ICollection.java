package com.github.gv2011.util.icol;

import static com.github.gv2011.util.icol.ICollections.toIList;
import static com.github.gv2011.util.icol.ICollections.toISetList;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import com.github.gv2011.util.XStream;
import com.github.gv2011.util.ex.ThrowingFunction;

public interface ICollection<E> extends Collection<E>{

  default Opt<E> asOpt(){
    final int size = size();
    if(size==0) return Opt.empty();
    else if(size==1) return Opt.of(iterator().next());
    else throw new IllegalStateException();
  }

  default E single(){
    return asOpt().get();
  }

  default E first(){
    if(isEmpty()) throw new NoSuchElementException();
    else return iterator().next();
  }

  default Opt<E> tryGetFirst(){
    if(isEmpty()) return Opt.empty();
    else return Opt.of(iterator().next());
  }

  default boolean containsElement(final E element){
    return contains(element);
  }

  <F> ISet<F> map(ThrowingFunction<? super E, ? extends F> mapping);

  @Override
  @Deprecated
  default boolean add(final E e) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default boolean addAll(final Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default boolean remove(final Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default boolean removeAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default boolean removeIf(final Predicate<? super E> filter) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default boolean retainAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  default XStream<E> stream() {
      return XStream.stream(spliterator(), false);
  }

  @Override
  default XStream<E> parallelStream() {
      return XStream.stream(spliterator(), true);
  }

  default IList<E> asList(){
    return stream().collect(toIList());
  }

  default ISetList<E> asSetList() {
    return stream().collect(toISetList());
  }


}
