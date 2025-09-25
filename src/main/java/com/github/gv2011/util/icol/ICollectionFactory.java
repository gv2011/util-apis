package com.github.gv2011.util.icol;

import static com.github.gv2011.util.CollectionUtils.stream;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Spliterator;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.gv2011.util.XStream;
import com.github.gv2011.util.icol.IList.Builder;


public interface ICollectionFactory {


  //Empty:

  Nothing nothing();

  @SuppressWarnings("unchecked")
  default <E> Empty<E> empty(){
    return nothing();
  }

  <E> ISetList<E> emptyList();

  @SuppressWarnings("unchecked")
  default <E> Empty<E> emptySet(){
    return nothing();
  }

  <E extends Comparable<? super E>> ISortedSet<E> emptySortedSet();

  <K,V> IMap<K,V> emptyMap();

  <K extends Comparable<? super K>,V> ISortedMap<K,V> emptySortedMap();


  //Single:

  <E> ISetList<E> listOf(final E element);

  default <E> Single<E> single(final E element){
    return setOf(element);
  }

  <E> Single<E> setOf(final E element);

  <E extends Comparable<? super E>> ISortedSet<E> sortedSetOf(final E element);

  <K,V> IMap<K,V> mapOf(final K key, V value);

  <K extends Comparable<? super K>,V> ISortedMap<K,V> sortedMapOf(final K key, V value);


  //Varargs:

  @SuppressWarnings("unchecked")
  default <E> IList<E> listOf(final E e0, final E e1, final E... more){
    return Stream.concat(Stream.of(e0,e1), Arrays.stream(more)).collect(listCollector());
  }

  default Path pathOf(final String... elements){
    return pathFrom(asList(elements));
  }

  @SuppressWarnings("unchecked")
  default <E> ISet<E> setOf(final E e0, final E e1, final E... more){
    return
      Stream.concat(
        Stream.of(e0, e1),
        StreamSupport.stream(Arrays.spliterator(more, 0, more.length), true)
      )
      .collect(setCollector())
    ;
  }

  @SuppressWarnings("unchecked")
  default <E extends Comparable<? super E>> ISortedSet<E> sortedSetOf(final E e0, final E e1, final E... more){
    return
      Stream.concat(
        Stream.of(e0, e1),
        StreamSupport.stream(Arrays.spliterator(more, 0, more.length), true)
      )
      .collect(sortedSetCollector())
    ;
  }


  //Optional:

  default <E> Opt<E> ofNullable(final E nullable){
    return nullable == null ? empty() : single(nullable);
  }

  default <E> Opt<E> ofOptional(final Optional<? extends E> optional){
    return optional.<Opt<E>>map(e->single(e)).orElseGet(()->emptySet());
  }

  default Opt<Integer> ofOptional(final OptionalInt optionalInt) {
    return optionalInt.isPresent() ? Opt.of(optionalInt.getAsInt()) : emptySet();
  }

  default <E> IList<E> listFrom(final Optional<? extends E> optional){
    return optional.map(e->listOf((E)e)).orElse(emptyList());
  }


  //Collections:

  default <E> IList<E> listFrom(final Collection<? extends E> collection){
    if(collection.isEmpty()) return emptyList();
    else if(collection.size()==1) return listOf(collection.iterator().next());
    else return collection.stream().collect(listCollector());
  }

  @SuppressWarnings("unchecked")
  default <E> ISet<E> setFrom(final Collection<? extends E> collection){
    if(ISet.class.isInstance(collection)) return (ISet<E>) collection;
    else{
      if(collection.isEmpty()) return emptySet();
      else if(collection.size()==1) return setOf(collection.iterator().next());
      else return collection.parallelStream().collect(setCollector());
    }
  }

  @SuppressWarnings("unchecked")
  default <E> ISetList<E> setListFrom(final Collection<? extends E> collection){
    if(ISetList.class.isInstance(collection)) return (ISetList<E>) collection;
    else{
      if(collection.isEmpty()) return emptyList();
      else if(collection.size()==1) return listOf(collection.iterator().next());
      else return collection.stream().collect(setListCollector());
    }
  }

  default <E extends Comparable<? super E>> ISortedSet<E> sortedSetFrom(final Collection<? extends E> collection){
    if(collection.isEmpty()) return emptySortedSet();
    else if(collection.size()==1) return sortedSetOf(collection.iterator().next());
    else return collection.parallelStream().collect(sortedSetCollector());
  }

  default <K,V> IMap<K,V> mapFrom(final Map<? extends K,? extends V> map){
    if(map.isEmpty()) return emptyMap();
    else if(map.size()==1){
      final Entry<? extends K, ? extends V> entry = map.entrySet().iterator().next();
      return mapOf(entry.getKey(), entry.getValue());
    }
    else return map.entrySet().parallelStream().collect(mapCollector());
  }

  default <K extends Comparable<? super K>,V> ISortedMap<K,V> sortedMapFrom(final Map<? extends K,? extends V> map){
    if(map.isEmpty()) return emptySortedMap();
    else if(map.size()==1){
      final Entry<? extends K, ? extends V> entry = map.entrySet().iterator().next();
      return sortedMapOf(entry.getKey(), entry.getValue());
    }
    else return map.entrySet().parallelStream().collect(sortedMapCollector());
  }


  //Arrays:

  default <E> IList<E> asList(final E[] elements){
    if(elements.length==0) return emptyList();
    else if(elements.length==1) return listOf(elements[0]);
    else return stream(elements).collect(listCollector());
  }

  default <E> ISet<E> asSet(final E[] elements){
    if(elements.length==0) return emptySet();
    else if(elements.length==1) return setOf(elements[0]);
    else return stream(elements).parallel().collect(setCollector());
  }

  default <E extends Comparable<? super E>> ISortedSet<E> asSortedSet(final E[] elements){
    if(elements.length==0) return emptySortedSet();
    else if(elements.length==1) return sortedSetOf(elements[0]);
    else return stream(elements).parallel().collect(sortedSetCollector());
  }


  //Legacy:

  default <E> IList<E> asList(final Enumeration<? extends E> elements){
    final Builder<E> b = listBuilder();
    while(elements.hasMoreElements()) b.add(elements.nextElement());
    return b.build();
  }


  //Builders:

  <E> IList.Builder<E> listBuilder();

  <E> ISetList.Builder<E> setListBuilder();

  Path.Builder pathBuilder();

  <E extends Comparable<? super E>> IComparableList.Builder<E> comparableListBuilder();

  <E> ISet.Builder<E> setBuilder();

  <E extends Comparable<? super E>> ISortedSet.Builder<E> sortedSetBuilder();

  <K,V> IMap.Builder<K,V> mapBuilder();

  <K extends Comparable<? super K>,V> ISortedMap.Builder<K,V> sortedMapBuilder();


  //Collectors:

  <E> Collector<E, ?, IList<E>> listCollector();

  <E> Collector<E, ?, ISetList<E>> setListCollector();

  <E> Collector<E, ?, ISet<E>> setCollector();

  <E extends Comparable<? super E>> Collector<E, ?, ISortedSet<E>> sortedSetCollector();

  <E, K, V>
  Collector<E, ?, IMap<K,V>> mapCollector(
    Function<? super E, ? extends K> keyMapper,
    Function<? super E, ? extends V> valueMapper
  );

  default <E extends Entry<? extends K, ? extends V>, K, V>
  Collector<E, ?, IMap<K,V>> mapCollector(){
    return mapCollector(Entry::getKey, Entry::getValue);
  }

  <E, K extends Comparable<? super K>, V>
  Collector<E, ?, ISortedMap<K,V>> sortedMapCollector(
    Function<? super E, ? extends K> keyMapper,
    Function<? super E, ? extends V> valueMapper
  );

  default <K extends Comparable<? super K>, V>
  Collector<Entry<? extends K, ? extends V>, ?, ISortedMap<K,V>> sortedMapCollector(){
    return sortedMapCollector(Entry::getKey, Entry::getValue);
  }

  Collector<String, ?, Path> pathCollector();


  //Other:

  <E> IList<E> filledList(E element, int size);

  Path emptyPath();

  Path pathFrom(final Collection<String> collection);

  <E> XStream<E> xStream(Stream<E> s);

  default <E> XStream<E> emptyStream(){
    return xStream(Stream.empty());
  }

  <E> XStream<E> pStream(Stream<E> s);

  <E> XStream<E> xStream(Spliterator<E> spliterator, boolean parallel);

  <K extends Comparable<? super K>,V> ISortedMap<K,V> priorityMerge(
    final IList<Stream<? extends V>> sources,
    final Function<? super V,? extends K> key,
    final BinaryOperator<V> mergeFunction
  );

  <E> Collector<E, ?, ISet<E>> transitiveClosure(final Function<E,Stream<E>> dependents);

}
