package com.github.gv2011.util.icol;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.ServiceLoaderUtils;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.ex.Exceptions;


/**
 * Convenience class with static methods that mirror the methods of the default {@link ICollectionFactory}.
 */
public final class ICollections {

  private ICollections(){Exceptions.staticClass();}

  private static final Constant<ICollectionFactory> ICOLF = Constants.softRefConstant(
      ()->ServiceLoaderUtils.loadService(ICollectionFactorySupplier.class).get()
  );

  public static final ICollectionFactory iCollections(){return ICOLF.get();}


  //Empty:

  public static Nothing nothing(){
    return iCollections().nothing();
  }

  @SuppressWarnings("unchecked")
  public static <E> Empty<E> empty(){
    return iCollections().nothing();
  }

  public static <E> ISetList<E> emptyList(){
    return iCollections().emptyList();
  }

  @SuppressWarnings("unchecked")
  public static <E> Empty<E> emptySet(){
    return iCollections().nothing();
  }

  public static <E extends Comparable<? super E>> ISortedSet<E> emptySortedSet() {
    return iCollections().emptySortedSet();
  }

  public static <K,V> IMap<K,V> emptyMap() {
    return iCollections().emptyMap();
  }

  public static <K extends Comparable<? super K>,V> ISortedMap<K,V> emptySortedMap() {
    return iCollections().emptySortedMap();
  }


  //Single:

  public static <E> ISetList<E> listOf(final E element) {
    return iCollections().listOf(element);
  }

  public static <E> Single<E> single(final E element){
    return iCollections().single(element);
  }

  public static <E> Single<E> setOf(final E element){
    return iCollections().single(element);
  }

  public static <E> ISet<E> addToSet(final Collection<? extends E> collection, final E element){
    final ISet<E> set = setFrom(collection);
    return set.contains(element)
      ? set
      : iCollections().<E>setBuilder().addAll(set).add(element).build()
    ;
  }

  public static <E extends Comparable<? super E>> ISortedSet<E> sortedSetOf(final E element) {
    return iCollections().sortedSetOf(element);
  }

  public static <K,V> IMap<K,V> mapOf(final K key, final V value) {
    return iCollections().mapOf(key, value);
  }

  public static <K extends Comparable<? super K>,V> ISortedMap<K,V> sortedMapOf(final K key, final V value) {
    return iCollections().sortedMapOf(key, value);
  }


  //Varargs:

  @SafeVarargs
  public static <E> IList<E> listOf(final E e1, final E e2, final E... more){
    return iCollections().listOf(e1, e2, more);
  }

  @SafeVarargs
  public static Path pathOf(final String... elements){
    return iCollections().pathOf(elements);
  }

  @SuppressWarnings("unchecked")
  public static <E> ISet<E> setOf(final E e1, final E e2, final E... more){
    return iCollections().setOf(e1, e2, more);
  }

  @SuppressWarnings("unchecked")
  public static <E extends Comparable<? super E>> ISortedSet<E> sortedSetOf(final E e1, final E e2, final E... more){
    return iCollections().sortedSetOf(e1, e2, more);
  }


  //Optional:

  public static <E> Opt<E> ofNullable(final @Nullable E nullable){
    return iCollections().ofNullable(nullable);
  }

  public static <E> Opt<E> ofOptional(final Optional<? extends E> optional){
    return iCollections().ofOptional(optional);
  }

  public static <E> IList<E> listFrom(final Optional<? extends E> optional){
    return iCollections().listFrom(optional);
  }


  //Collections:

  public static <E> IList<E> listFrom(final Collection<? extends E> collection){
    return iCollections().listFrom(collection);
  }

  public static <E> IList<E> listFrom(final Iterator<? extends E> iterator){
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false).collect(toIList());
  }

  public static Path pathFrom(final java.nio.file.Path filePath){
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(filePath.iterator(), 0), false)
      .map(java.nio.file.Path::toString)
      .collect(toPath())
    ;
  }

  public static Path pathFrom(final Collection<String> collection){
    return iCollections().pathFrom(collection);
  }

  public static <E> ISet<E> setFrom(final Collection<? extends E> collection){
    return iCollections().setFrom(collection);
  }

  public static <E> ISetList<E> setListFrom(final Collection<? extends E> collection){
    return iCollections().setListFrom(collection);
  }

  public static <E extends Comparable<? super E>> ISortedSet<E> sortedSetFrom(
    final Collection<? extends E> collection
  ){
    return iCollections().sortedSetFrom(collection);
  }

  public static <K,V> IMap<K,V> mapFrom(final Map<? extends K,? extends V> map){
    return iCollections().mapFrom(map);
  }

  public static <K extends Comparable<? super K>,V> ISortedMap<K,V> sortedMapFrom(
    final Map<? extends K,? extends V> map
  ){
    return iCollections().sortedMapFrom(map);
  }

  //Arrays:

  public static <E> IList<E> asList(final E[] elements){
    return iCollections().asList(elements);
  }

  public static <E> ISet<E> asSet(final E[] elements){
    return iCollections().asSet(elements);
  }

  public static <E extends Comparable<? super E>> ISortedSet<E> asSortedSet(final E[] elements){
    return iCollections().asSortedSet(elements);
  }


  //Legacy:

  public static <E> IList<E> asList(final Enumeration<? extends E> elements){
    return iCollections().asList(elements);
  }


  //Builders:

  public static <E> IList.Builder<E> listBuilder() {
    return iCollections().listBuilder();
  }

  public static <E> ISetList.Builder<E> setListBuilder() {
    return iCollections().setListBuilder();
  }

  public static Path.Builder pathBuilder() {
    return iCollections().pathBuilder();
  }

  public static <E extends Comparable<? super E>> IComparableList.Builder<E> comparableListBuilder() {
    return iCollections().comparableListBuilder();
  }

  public static <E> ISet.Builder<E> setBuilder() {
    return iCollections().setBuilder();
  }

  public static <E extends Comparable<? super E>> ISortedSet.Builder<E> sortedSetBuilder() {
    return iCollections().sortedSetBuilder();
  }

  public static <K,V> IMap.Builder<K,V> mapBuilder() {
    return iCollections().mapBuilder();
  }

  public static <K extends Comparable<? super K>,V> ISortedMap.Builder<K,V> sortedMapBuilder() {
    return iCollections().sortedMapBuilder();
  }


  //Collectors:

  public static <E> Collector<E, ?, IList<E>> toIList() {
    return iCollections().listCollector();
  }

  public static <E> Collector<E, ?, ISetList<E>> toISetList() {
    return iCollections().setListCollector();
  }

  public static <E> Collector<E, ?, ISet<E>> toISet() {
    return iCollections().setCollector();
  }

  public static <E> Collector<E, ?, ISet<E>> transitiveClosure(final Function<? super E,Stream<? extends E>> dependents) {
    return iCollections().transitiveClosure(dependents);
  }

  public static <E> ISet<E> transitiveClosure(final E node, final Function<? super E,Stream<? extends E>> dependents) {
    return Stream.of(node).collect(transitiveClosure(dependents));
  }

  public static <E extends Comparable<? super E>> Collector<E, ?, ISortedSet<E>> toISortedSet() {
    return iCollections().sortedSetCollector();
  }

  public static <E, K, V>
  Collector<E, ?, IMap<K,V>> toIMap(
    final Function<? super E, ? extends K> keyMapper,
    final Function<? super E, ? extends V> valueMapper
  ){
    return iCollections().mapCollector(keyMapper, valueMapper);
  }

  public static <E extends Entry<? extends K, ? extends V>, K, V>
  Collector<E, ?, IMap<K,V>> toIMap(){
    return iCollections().mapCollector();
  }

  public static <E, K extends Comparable<? super K>, V>
  Collector<E, ?, ISortedMap<K,V>> toISortedMap(
    final Function<? super E, ? extends K> keyMapper,
    final Function<? super E, ? extends V> valueMapper
  ){
    return iCollections().sortedMapCollector(keyMapper, valueMapper);
  }

  public static <K extends Comparable<? super K>, V>
  Collector<Entry<? extends K, ? extends V>, ?, ISortedMap<K,V>> toISortedMap(){
    return iCollections().sortedMapCollector();
  }

  public static Collector<String, ?, Path> toPath() {
    return iCollections().pathCollector();
  }


  //Upcast:

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static final <U,E extends U> Opt<U> upcast(final Opt<E> optional){
    return (Opt)optional;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static final <U,E extends U> IList<U> upcast(final IList<E> list){
    return (IList)list;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static final <U> ISet<U> upcast(final ISet<? extends U> set){
    return (ISet)set;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static final <U extends Comparable<? super U>,E extends U> ISortedSet<U> upcast(final ISortedSet<E> set){
    return (ISortedSet)set;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static final <UK,K extends UK,UV, V extends UV> IMap<UK,UV> upcast(final IMap<K,V> map){
    return (IMap)map;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static final <UK extends Comparable<? super UK>,K extends UK,UV, V extends UV> ISortedMap<UK,UV>
    upcast(final ISortedMap<K,V> map
  ){
    return (ISortedMap)map;
  }

  //Other:

  public static <E> IList<E> filledList(final E element, final int size){
    return iCollections().filledList(element, size);
  }

  public static Path emptyPath() {
    return iCollections().emptyPath();
  }

  public static <E> XStream<E> xStream(final Stream<E> s) {
    return iCollections().xStream(s);
  }

  public static <E> XStream<E> emptyStream() {
    return iCollections().emptyStream();
  }

  public static <E> XStream<E> xStream(final Collection<E> c) {
    return iCollections().xStream(c.stream());
  }

  public static <E> XStream<E> pStream(final Stream<E> s) {
    return iCollections().pStream(s);
  }

  public static <E> XStream<E> xStream(final Spliterator<E> spliterator, final boolean parallel) {
    return iCollections().xStream(spliterator, parallel);
  }

  public static <E> ISet<E> intersection(final ICollection<E> first, final Collection<?> second) {
    return first.parallelStream().filter(second::contains).collect(toISet());
  }

  public static <C extends Comparable<? super C>> ISortedSet<C> sortedIntersection(
    final ICollection<C> first, final Collection<?> second
  ) {
    return first.parallelStream().filter(second::contains).collect(toISortedSet());
  }

  public static <K extends Comparable<? super K>,V> ISortedMap<K,V> priorityMerge(
    final IList<Stream<? extends V>> sources,
    final Function<? super V,? extends K> key
  ){
    return iCollections().priorityMerge(sources, key, (v0,v1)->v0);
  }

  public static <K extends Comparable<? super K>,V> ISortedMap<K,V> priorityMerge(
    final IList<Stream<? extends V>> sources,
    final Function<? super V,? extends K> key,
    final BinaryOperator<V> mergeFunction
  ){
    return iCollections().priorityMerge(sources, key, mergeFunction);
  }



}
