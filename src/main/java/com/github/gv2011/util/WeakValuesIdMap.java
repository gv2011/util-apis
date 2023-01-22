package com.github.gv2011.util;


import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.nothing;

import com.github.gv2011.util.icol.Nothing;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Function;

import com.github.gv2011.util.icol.Opt;

public final class WeakValuesIdMap<ID extends Comparable<? super ID>,V>
extends AbstractMap<ID,V> implements NullSafeMap<ID,V>{

  private final EntrySet entrySet = new EntrySet();
  private final Map<V,Nothing> values = new WeakHashMap<>();
  private final Function<V,ID> idFunction;
  private SoftReference<NavigableMap<ID,WeakReference<V>>> indexRef = new SoftReference<>(null);


  public WeakValuesIdMap(final Function<V, ID> idFunction) {
    this.idFunction = idFunction;
  }


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
      tryGetIndex().ifPresentDo(i->i.put(idFunction.apply(value), new WeakReference<>(value)));
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
      removed.ifPresentDo(v->values.remove(v));
      return removed;
    }
  }

  private NavigableMap<ID, WeakReference<V>> getIndex(){
    NavigableMap<ID, WeakReference<V>> index = indexRef.get();
    if(index==null){
      index = values.keySet().stream().collect(CollectionUtils.toSortedMap(idFunction, WeakReference::new));
      indexRef = new SoftReference<>(index);
    }
    return index;
  }

  private Opt<NavigableMap<ID, WeakReference<V>>> tryGetIndex() {
    return Opt.ofNullable(indexRef.get());
  }

  private Iterator<Entry<ID, V>> newEntrySetIterator() {
    return new EntrySetIterator();
  }

  @Override
  public final Opt<V> tryGet(final Object key) {
    synchronized(values){
      final Opt<V> result = Opt.ofNullable(getIndex().get(key)).map(r->Opt.ofNullable(r.get())).orElse(Opt.empty());
      if(result.isPresent()) {
        verify(values.containsKey(result.get()));
        verifyEqual(idFunction.apply(result.get()), key);
      }
      else assert values.keySet().stream().noneMatch(v->idFunction.apply(v).equals(key));
      assert Opt.ofNullable(super.get(key)).equals(result);
      return result;
    }
  }

  @Override
  public final int size() {
    synchronized(values){
      return values.size();
    }
  }


  private final class EntrySet extends AbstractSet<Entry<ID, V>>{

    @Override
    public Iterator<Entry<ID, V>> iterator() {
      return newEntrySetIterator();
    }

    @Override
    public int size() {
      return WeakValuesIdMap.this.size();
    }
  }


  private final class EntrySetIterator implements Iterator<Entry<ID, V>>{
    private Opt<V> lastDelivered = Opt.empty();
    private Opt<V> next = tryFindUndeliveredValue(lastDelivered);

    @Override
    public boolean hasNext() {
      return next!=null;
    }

    @Override
    public Entry<ID, V> next() {
      if(!next.isPresent()) throw new NoSuchElementException();
      lastDelivered = next;
      next = tryFindUndeliveredValue(lastDelivered);
      return new MapEntry(lastDelivered.get());
    }

    @Override
    public void remove() {
      final V last = lastDelivered.orElseThrow(IllegalStateException::new);
      lastDelivered = Opt.empty();
      synchronized(values){
        values.remove(last);
        tryGetIndex().ifPresentDo(i->i.remove(idFunction.apply(last)));
      }
    }
  }


  private Opt<V> tryFindUndeliveredValue(final Opt<V> lastDelivered) {
    synchronized(values){
      return
        XStream.xStream(
          lastDelivered
          .map(l->getIndex().tailMap(idFunction.apply(l), false))
          .orElseGet(this::getIndex)
          .entrySet().stream()
        )
        .flatOpt(e->Opt.ofNullable(e.getValue().get()))
        .tryFindFirst()
      ;
    }
  }


  private final class MapEntry implements Entry<ID, V>{
    private final V value;

    private MapEntry(final V value) {
      this.value = value;
    }

    @Override
    public ID getKey() {
      return idFunction.apply(value);
    }

    @Override
    public V getValue() {
      return value;
    }

    @Override
    public boolean equals(final Object o) {
      if(this==o) return true;
      else if (!(o instanceof Map.Entry)) return false;
      else{
        final Map.Entry<?,?> e = (Map.Entry<?,?>)o;
        return Objects.equals(value, e.getValue()) && Objects.equals(getKey(), e.getKey());
      }
    }

    @Override
    public int hashCode() {
      return getKey().hashCode() ^ value.hashCode();
    }

    @Override
    public V setValue(final V value) {
      throw new UnsupportedOperationException();
    }
  }

}
