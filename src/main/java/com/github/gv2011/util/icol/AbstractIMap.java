package com.github.gv2011.util.icol;

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.icol.ICollections.toIList;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static java.util.stream.Collectors.joining;

import java.util.Map;

import com.github.gv2011.util.Equal;

public abstract class AbstractIMap<K, V> implements IMap<K,V>{

  @Override
  public abstract ISet<K> keySet();

  @Override
  public abstract Opt<V> tryGet(final Object key);

  @Override
  public ISet<Entry<K, V>> entrySet() {
    return keySet().stream()
      .map(k->(Entry<K, V>)pair(k, get(k)))
      .collect(toISet())
    ;
  }

  @Override
  public Entry<K, V> single() {
    return entrySet().single();
  }

  @Override
  public int size() {
    return keySet().size();
  }

  @Override
  public boolean isEmpty() {
    return keySet().isEmpty();
  }

  @Override
  public boolean containsKey(final Object key) {
    return keySet().contains(key);
  }

  @Override
  public boolean containsValue(final Object value) {
    return values().contains(value);
  }

  @Override
  public IList<V> values() {
    return keySet().stream().map(this::get).collect(toIList());
  }


  @Override
  public int hashCode() {
    return entrySet().parallelStream().mapToInt(Entry::hashCode).sum();
  }

  @Override
  public boolean equals(final Object obj) {
    return Equal.equal(this, obj, Map.class, m->{
      if(size()!=m.size()) return false;
      else if(!keySet().equals(m.keySet())) return false;
      else return keySet().stream().allMatch(k->get(k).equals(m.get(k)));
    });
  }

  @Override
  public String toString() {
    return keySet().stream()
      .map(k->k + IEntry.KEY_VALUE_SEPARATOR + get(k))
      .collect(joining(IMap.ENTRY_SEPARATOR, IMap.PREFIX, IMap.SUFFIX))
    ;
  }

}
