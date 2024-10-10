package com.github.gv2011.util.icol;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.icol.ICollections.toIList;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static com.github.gv2011.util.icol.ICollections.toISortedMap;
import static java.util.stream.Collectors.joining;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.IntStream;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.ex.ThrowingFunction;


public abstract class AbstractIList<E> implements IList<E>{

  private final Constant<Integer> hash = Constants.cachedConstant(()->{
    int hashCode = 1;
    for (final E e : this) hashCode = 31*hashCode + e.hashCode();
    return hashCode;
  });

  @Override
  public abstract int size();

  @Override
  public abstract E get(final int index);

  @Override
  public IList<E> subList(final int fromIndex, final int toIndex) {
    return IntStream.range(fromIndex, toIndex).mapToObj(this::get).collect(toIList());
  }


  @Override
  public boolean isEmpty() {
    return size()==0;
  }

  @Override
  public boolean contains(final Object o) {
    if(isEmpty()) return false;
    else return stream().anyMatch(o::equals);
  }

  @Override
  public Iterator<E> iterator() {
    return listIterator();
  }

  @Override
  public Object[] toArray() {
    final Object[] result = new Object[size()];
    for(int i=0; i<size(); i++) result[i] = get(i);
    return result;
  }

  @Override
  public boolean containsAll(final Collection<?> c) {
    if(this==c) return true;
    else if(isEmpty()) return c.isEmpty();
    else{
      return c.stream().allMatch(this::contains);
    }
  }


  @Override
  public int indexOf(final Object o) {
    return IntStream.range(0, size()).filter(i->o.equals(get(i))).findFirst().orElse(-1);
  }

  @Override
  public int lastIndexOf(final Object o) {
    final int s = size();
    return IntStream.range(1, s-1).map(i->s-i).filter(i->o.equals(get(i))).findFirst().orElse(-1);
  }



  @Override
  public ListIterator<E> listIterator() {
    return listIterator(0);
  }

  @Override
  public ListIterator<E> listIterator(final int index) {
    return new ListIterator<>(){
      private int i = index;
      @Override
      public boolean hasNext() {
        return i<size();
      }
      @Override
      public E next() {
        verify(hasNext());
        return get(i++);
      }
      @Override
      public boolean hasPrevious() {
        return i>0;
      }
      @Override
      public E previous() {
        verify(hasPrevious());
        return get(--i);
      }
      @Override
      public int nextIndex() {
        return i;
      }

      @Override
      public int previousIndex() {
        return i-1;
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

    };
  }

  @Override
  public ISortedMap<Integer, E> asMap() {
    return IntStream.range(0, size()).parallel().boxed().collect(toISortedMap(
      i->i,
      this::get
    ));
  }

  @Override
  public final <F> ISet<F> map(final ThrowingFunction<? super E, ? extends F> mapping) {
    return stream().map(mapping.asFunction()).collect(toISet());
  }

  @Override
  public String toString() {
    return stream().map(Object::toString).collect(joining(", ","[","]"));
  }

  @Override
  public boolean equals(final Object o) {
    boolean result;
    if (o == this) result = true;
    else if (!(o instanceof List)) result = false;
    else {
      final List<?> other = ((List<?>) o);
      if(size()!=other.size()) result = false;
      else if(hashCode()!=other.hashCode()) result = false;
      else {
        result = IntStream.range(0, size()).parallel().allMatch(i->get(i).equals(other.get(i)));
      }
    }
    return result;
  }

  @Override
  public int hashCode() {
    return hash.get();
  }

  @Override
  public ISet<E> intersection(final Collection<?> other) {
    return ICollections.intersection(this, other);
  }

  @Override
  public IList<E> reversed() {
    return new ReversedIList();
  }

  private final class ReversedIList extends AbstractIList<E>{
    @Override
    public E get(final int index) {return AbstractIList.this.get(AbstractIList.this.size()-1-index);}
    @Override
    public int size() {return AbstractIList.this.size();}
    @Override
    public IList<E> reversed() {return AbstractIList.this;}
  }

}
