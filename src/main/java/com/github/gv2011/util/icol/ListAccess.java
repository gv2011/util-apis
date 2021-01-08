package com.github.gv2011.util.icol;

import static com.github.gv2011.util.CollectionUtils.intRange;

import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import com.github.gv2011.util.XStream;

public interface ListAccess<E> {
  
  default boolean isEmpty(){
    return size()==0;
  }

  int size();

  E get(int index);

  IList<E> subList(int fromIndex, int toIndex);

  ISortedMap<Integer,E> asMap();

  default IList<E> tail(){
    return subList(1, size());
  }

  /**
   * @return index of first occurence of <ode>obj</code> or -1 if it is not in the collection.
   */
  default int indexOf(final Object obj){
    return IntStream.range(0,size()).filter(i->get(i).equals(obj)).findFirst().orElse(-1);
  }

  /**
   * @return index of first occurence of <ode>element</code> or -1 if it is not in the collection.
   */
  default int indexOfElement(final E element){
    return indexOf(element);
  }

  /**
   * @return index of last occurence of <ode>obj</code> or -1 if it is not in the list.
   */
  default int lastIndexOf(final Object obj) {
    final int size = size();
    return intRange(size-1,0).filter(i->get(i).equals(obj)).findFirst().orElse(-1);
  }

  /**
   * @return index of last occurence of <ode>element</code> or -1 if it is not in the list.
   */
  default int lastIndexOfElement(final E element) {
    return lastIndexOf(element);
  }
  
  default E last(){
    if(isEmpty()) throw new NoSuchElementException();
    else return get(size()-1);
  }


  XStream<E> stream();

}
