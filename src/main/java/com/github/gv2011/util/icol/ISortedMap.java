package com.github.gv2011.util.icol;

import java.util.Comparator;
import java.util.NavigableMap;


public interface ISortedMap<K extends Comparable<? super K>,V> extends IMap<K,V>, NavigableMap<K,V>{

  public static interface Builder<K extends Comparable<? super K>,V>
  extends MapBuilder<ISortedMap<K,V>,K,V,Builder<K,V>>{}

  @SuppressWarnings("unchecked")
  static <K extends Comparable<? super K>,V> ISortedMap<K,V>
  cast(final ISortedMap<? extends K, ? extends V> map){return (ISortedMap<K, V>) map;}

  @Override
  ISortedSet<K> keySet();

  @Override
  IList<V> values();

  @Override
  default Entry<K, V> lowerEntry(final K key) {
    return tryGetLowerEntry(key).get();
  }

  Opt<Entry<K, V>> tryGetLowerEntry(final K key);

  @Override
  default K lowerKey(final K key) {
    return tryGetLowerKey(key).get();
  }

  Opt<K> tryGetLowerKey(final K key);

  @Override
  default Entry<K, V> floorEntry(final K key) {
    return tryGetFloorEntry(key).get();
  }

  Opt<Entry<K, V>> tryGetFloorEntry(final K key);

  @Override
  default K floorKey(final K key) {
    return tryGetFloorKey(key).get();
  }

  Opt<K> tryGetFloorKey(final K key);

  @Override
  default K firstKey(){
    return tryGetFirstKey().get();
  }
  Opt<K> tryGetFirstKey();

  @Override
  default K lastKey(){
    return tryGetLastKey().get();
  }
  Opt<K> tryGetLastKey();

  @Override
  default Entry<K, V> ceilingEntry(final K key) {
    return tryGetCeilingEntry(key).get();
  }

  Opt<Entry<K, V>> tryGetCeilingEntry(final K key);

  @Override
  default K ceilingKey(final K key) {
    return tryGetCeilingKey(key).get();
  }

  Opt<K> tryGetCeilingKey(final K key);

  @Override
  default Entry<K, V> higherEntry(final K key) {
    return tryGetHigherEntry(key).get();
  }

  Opt<Entry<K, V>> tryGetHigherEntry(final K key);

  @Override
  default K higherKey(final K key) {
    return tryGetHigherKey(key).get();
  }

  Opt<K> tryGetHigherKey(final K key);

  @Override
  default Entry<K, V> firstEntry() {
    return tryGetFirstEntry().get();
  }

  Opt<Entry<K, V>> tryGetFirstEntry();

  @Override
  default Entry<K, V> lastEntry() {
    return tryGetLastEntry().get();
  }

  Opt<Entry<K, V>> tryGetLastEntry();

  @Override
  default ISortedSet<K> navigableKeySet(){
    return keySet();
  }

  @Override
  default ISortedMap<K, V> subMap(final K fromKey, final K toKey){
    return subMap(fromKey, true, toKey, false);
  }

  @Override
  ISortedMap<K, V> subMap(final K fromKey, final boolean fromInclusive, final K toKey, final boolean toInclusive);

  @Override
  default ISortedMap<K, V> headMap(final K toKey){
    return headMap(toKey, false);
  }

  @Override
  default ISortedMap<K, V> headMap(final K toKey, final boolean inclusive){
    if(isEmpty()) return this;
    else return subMap(firstKey(), true, toKey, inclusive);
  }

  @Override
  default ISortedMap<K, V> tailMap(final K fromKey){
    return tailMap(fromKey, true);
  }

  @Override
  default ISortedMap<K, V> tailMap(final K fromKey, final boolean inclusive){
    if(isEmpty()) return this;
    else return subMap(fromKey, inclusive, lastKey(), true);
  }

  @Deprecated
  @Override
  default Comparator<? super K> comparator() {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default Entry<K, V> pollFirstEntry() {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default Entry<K, V> pollLastEntry() {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default ISortedMap<K, V> descendingMap() {
    throw new UnsupportedOperationException();
  }

  @Override
  default ISortedSet<K> descendingKeySet(){
    throw new UnsupportedOperationException();
  }

}
