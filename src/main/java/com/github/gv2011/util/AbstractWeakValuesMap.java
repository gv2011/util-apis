package com.github.gv2011.util;

import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.Nothing.nothing;

import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Function;

import com.github.gv2011.util.icol.Nothing;
import com.github.gv2011.util.icol.Opt;

abstract class AbstractWeakValuesMap<ID extends Comparable<? super ID>,V>
extends AbstractMap<ID,V> implements NullSafeMap<ID,V>{

  private final EntrySet entrySet = new EntrySet();

  final Map<V,Nothing> values = new WeakHashMap<>();

  final Function<V,ID> idFunction;

  AbstractWeakValuesMap(final Function<V, ID> idFunction) {
    this.idFunction = idFunction;
  }

  abstract NavigableMap<ID, WeakReference<V>> getIndex();

  abstract Opt<NavigableMap<ID, WeakReference<V>>> tryGetIndex();

  abstract Iterator<Entry<ID, V>> newEntrySetIterator();

  @Override
  public final V put(final ID key, final V value) {
    synchronized(values){
      verifyEqual(key, idFunction.apply(value));
      if(values.containsKey(value)){
        return value;
      }
      else{
        add(value);
        return null;
      }
    }
  }

  public final void add(final V value) {
    synchronized(values){
      values.put(value, nothing());
      tryGetIndex().ifPresent(i->i.put(idFunction.apply(value), new WeakReference<>(value)));
    }
  }

  @Override
  public final Set<Entry<ID, V>> entrySet() {
    return entrySet;
  }

  @Override
  public final V get(final Object key) {
    return tryGet(key).orElseThrow(()->new NoSuchElementException(format("No entry for key {}.", key)));
  }

  @Override
  public final V remove(final Object key) {
    return tryRemove(key).orElseThrow(()->new NoSuchElementException(format("No entry for key {}.", key)));
  }

  @Override
  public final Opt<V> tryRemove(final Object key) {
    synchronized(values){
      final Opt<V> removed = Opt.ofNullable(getIndex().remove(key)).flatMap(r->Opt.ofNullable(r.get()));
      removed.ifPresent(v->values.remove(v));
      return removed;
    }
  }

  @Override
  public abstract int size();

  private final class EntrySet extends AbstractSet<Entry<ID, V>>{

    @Override
    public Iterator<Entry<ID, V>> iterator() {
      return newEntrySetIterator();
    }

    @Override
    public int size() {
      return AbstractWeakValuesMap.this.size();
    }
  }

}
