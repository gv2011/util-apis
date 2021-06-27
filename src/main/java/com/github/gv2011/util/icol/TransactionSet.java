package com.github.gv2011.util.icol;

import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.iCollections;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static com.github.gv2011.util.icol.Nothing.nothing;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.lock.Lock;

/**
 * Fast, never-blocking read access.
 * Concurrent modifications only block when the same element is affected.
 */
public class TransactionSet<T> extends AbstractSet<T>{

  private final Lock lock = Lock.create();

  private volatile ISet<T> current = ICollections.emptySet();

  private final Set<T> pending = new HashSet<>();

  /**
   * Iterates over a snapshot.
   */
  @Override
  public Iterator<T> iterator() {
    return new It(current.iterator());
  }

  @Override
  public int size() {
    return current.size();
  }

  @Override
  public boolean contains(final Object o) {
    return current.contains(o);
  }

  @Override
  public boolean containsAll(final Collection<?> c) {
    return current.containsAll(c);
  }

  @Override
  public boolean add(final T e) {
    return add(e, e1->{});
  }

  /**
   * Elements are only added after succesful operation.
   * Blocks while other threads add or delete an overlapping set of elements.
   */
  public boolean add(final T element, final Consumer<T> operation) {
    return addAll(
      iCollections().setOf(element),
      a->{
        if(!a.isEmpty()){
          assert a.single().equals(element);
          operation.accept(element);
        }
      }
    );
  }

  @Override
  public boolean addAll(final Collection<? extends T> c) {
    return addAll(iCollections().setFrom(c), a->{});
  }


/**
 * Operation is only done if element is not present.
 * Element is only added after succesful operation.
 * Blocks while other thread adds or deletes the same element.
 * @param operation is called with all elements that will be added.
 * @return true, if at least one element was added.
 */
public boolean addAll(final ICollection<? extends T> elements, final Consumer<ISet<T>> operation) {
    lock.callWhen(containsNone(pending, elements), ()->{
      pending.addAll(elements);
      return nothing();
    });
    //elements are locked now
    try{
      final ISet<T> toAdd;
      {	final Set<T> s = new HashSet<>(elements);
      	s.removeAll(current);
      	toAdd = iCollections().setFrom(s);
      }
      operation.accept(iCollections().setFrom(toAdd));
      lock.run(()->{
    	final int expectedSize = current.size() + toAdd.size();
        current = iCollections().<T>setBuilder()
          .addAll(current)
          .addAll(toAdd)
          .build()
        ;
        verifyEqual(current.size(),expectedSize);
      });
      return !toAdd.isEmpty();
    }
    finally{
      lock.run(()->pending.removeAll(elements), true);
      //elements are unlocked now
    }
  }

  private Supplier<Boolean> containsNone(final Set<T> set, final Collection<? extends T> elements) {
    return new Supplier<Boolean>(){
      @Override
      public Boolean get() {
        return elements.stream().noneMatch(set::contains);
      }
      @Override
      public String toString() {
        return format("(Condition: {} contains none of {})", set, elements);
      }
    };
  }

  @Override
  public boolean remove(final Object o) {
    return remove(o, e->{});
  }

  /**
   * Operation is only done if element is present.
   * Element is only removed after succesful operation.
   * Blocks while other thread adds or deletes the same element.
   */
  public boolean remove(final Object o, final Consumer<T> operation) {
    final boolean removed;
    final boolean present = lock.call(()->current.contains(o));
    if(!present) removed = false;
    else{
      @SuppressWarnings("unchecked")
      final T e = (T)o;
      lock.callWhen(()->!pending.contains(e), ()->{
        pending.add(e);
        return nothing();
      });
      try{
        final boolean remove = lock.call(()->current.contains(e));
        if(remove){
          operation.accept(e);
          lock.run(()->{
            assert current.contains(e);
            current = current.stream().filter(a->!a.equals(e)).collect(toISet());
            assert !current.contains(e);
          });
          removed = true;
        }
        else removed = false;
      }
      finally{
        lock.run(()->pending.remove(e), true);
      }
    }
    return removed;
  }



  private final class It implements Iterator<T>{
    private final Iterator<T> delegate;
    private volatile @Nullable T last = null;

    private It(final Iterator<T> delegate) {
      this.delegate = delegate;
    }

    @Override
    public boolean hasNext() {
      return delegate.hasNext();
    }

    @Override
    public T next() {
      final T next = delegate.next();
      last = next;
      return next;
    }

    @Override
    public void remove() {
      TransactionSet.this.remove(notNull(last));
    }

  }

}
