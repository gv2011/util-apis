package com.github.gv2011.util.icol;


import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.icol.ICollections.toISortedMap;

import java.util.Comparator;


public abstract class AbstractISortedMap<K extends Comparable<? super K>, V>
extends AbstractIMap<K,V>
implements ISortedMap<K,V>{

  @Override
  public abstract ISortedSet<K> keySet();


  @Override
  public Opt<Entry<K, V>> tryGetLowerEntry(final K key) {
    return keySet().tryGetLower(key).map(k->(Entry<K,V>)pair(k, get(k)));
  }

  @Override
  public Opt<K> tryGetLowerKey(final K key) {
    return keySet().tryGetLower(key);
  }

  @Override
  public Opt<Entry<K, V>> tryGetFloorEntry(final K key) {
    return keySet().tryGetFloor(key).map(k->(Entry<K,V>)pair(k, get(k)));
  }

  @Override
  public Opt<K> tryGetFloorKey(final K key) {
    return keySet().tryGetFloor(key);
  }

  @Override
  public Opt<K> tryGetFirstKey() {
    return keySet().tryGetFirst();
  }

  @Override
  public Opt<K> tryGetLastKey() {
    return keySet().tryGetLast();
  }

  @Override
  public Opt<Entry<K, V>> tryGetCeilingEntry(final K key) {
    return keySet().tryGetCeiling(key).map(k->(Entry<K,V>)pair(k, get(k)));
  }

  @Override
  public Opt<K> tryGetCeilingKey(final K key) {
    return keySet().tryGetCeiling(key);
  }

  @Override
  public Opt<Entry<K, V>> tryGetHigherEntry(final K key) {
    return keySet().tryGetHigher(key).map(k->(Entry<K,V>)pair(k, get(k)));
  }

  @Override
  public Opt<K> tryGetHigherKey(final K key) {
    return keySet().tryGetHigher(key);
  }

  @Override
  public Opt<Entry<K, V>> tryGetFirstEntry() {
    return keySet().tryGetFirst().map(k->(Entry<K,V>)pair(k, get(k)));
  }

  @Override
  public Opt<Entry<K, V>> tryGetLastEntry() {
    return keySet().tryGetLast().map(k->(Entry<K,V>)pair(k, get(k)));
  }

  @Override
  public ISortedMap<K, V> subMap(
    final K fromKey, final boolean fromInclusive, final K toKey, final boolean toInclusive
  ) {
    return keySet().subSet(fromKey, fromInclusive, toKey, toInclusive).stream().collect(toISortedMap(
      k->k,
      this::get
    ));
  }

  @Deprecated
  @Override
  public final Comparator<? super K> comparator() {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public final Entry<K, V> pollFirstEntry() {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public final Entry<K, V> pollLastEntry() {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public final ISortedMap<K, V> descendingMap() {
    throw new UnsupportedOperationException();
  }

  @Override
  public final ISortedSet<K> descendingKeySet(){
    throw new UnsupportedOperationException();
  }

}
