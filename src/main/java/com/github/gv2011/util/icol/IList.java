package com.github.gv2011.util.icol;

import static com.github.gv2011.util.CollectionUtils.intRange;
import static com.github.gv2011.util.icol.ICollections.toIList;
import static com.github.gv2011.util.icol.ICollections.toISortedMap;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

import com.github.gv2011.util.XStream;

public interface IList<E> extends List<E>, ICollectionG<E,IList<E>>, ListAccess<E>{

  public static interface Builder<E> extends CollectionBuilder<IList<E>,E,Builder<E>>{

    int size();

    E get(int index);

    E set(int index, E element);

    default E getLast(){return get(size()-1);}

    default E setLast(final E element){return set(size()-1, element);}

  }

  @Override
  default ISortedMap<Integer,E> asMap(){
    return IntStream.range(0, size()).parallel().mapToObj(Integer::valueOf)
      .collect(toISortedMap(
          i->i,
          this::get
      ))
    ;
  }

  @Deprecated
  @Override
  default boolean add(final E e) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default boolean addAll(final Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default boolean addAll(final int index, final Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default boolean retainAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default void replaceAll(final UnaryOperator<E> operator) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default void sort(final Comparator<? super E> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default void clear() {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default E set(final int index, final E element) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default void add(final int index, final E element) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default E remove(final int index) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default boolean removeAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default boolean remove(final Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  default boolean isEmpty(){
    return size()==0;
  }
  
  @Override
  default IList<E> tail(){
    return subList(1, size());
  }

  @Override
  default IList<E> asList() {
    return this;
  }

  @Override
  default Object[] toArray() {
    final int size = size();
    final Object[] result = new Object[size];
    for(int i=0; i<size; i++) result[i]=get(i);
    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  default <T> T[] toArray(T[] a) {
    final int size = size();
    if(a.length<size){
      a = (T[]) Array.newInstance(a.getClass().getComponentType(),size);
    }
    for(int i=0; i<size; i++) a[i]=(T) get(i);
    if(a.length>size) a[size] = null;
    return a;
  }


  @Override
  default int indexOf(final Object o){
    return IntStream.range(0,size()).filter(i->get(i).equals(o)).findFirst().orElse(-1);
  }

  @Override
  default int lastIndexOf(final Object o) {
    return intRange(size()-1,-1).filter(i->get(i).equals(o)).findFirst().orElse(-1);
  }

  @Override
  default E last(){
    if(isEmpty()) throw new NoSuchElementException();
    else return get(size()-1);
  }


  @Override
  default IList<E> addElement(final E element) {
    return ICollections.<E>listBuilder().addAll(this).add(element).build();
  }

  @Override
  default XStream<E> stream() {
      return XStream.stream(spliterator(), false);
  }


  @Override
  default XStream<E> parallelStream() {
      return XStream.stream(spliterator(), true);
  }

  @Override
  default IList<E> join(final Collection<? extends E> other) {
    return stream().concat(other.stream()).collect(toIList());
  }

  @Override
  default IList<E> subtract(final Collection<?> other) {
    return stream().filter(e->!other.contains(e)).collect(toIList());
  }

  IList<E> reversed();

}
