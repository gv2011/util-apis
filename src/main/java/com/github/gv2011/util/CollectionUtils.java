package com.github.gv2011.util;

import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.icol.ICollections.toIList;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collector.Characteristics.*;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import com.github.gv2011.util.icol.ICollection;
import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.Opt;

public final class CollectionUtils {

  private CollectionUtils(){staticClass();}

  private static abstract class OptCollector<T,R> implements Collector<T,AtomicReference<T>,R>{
    @Override
    public BiConsumer<AtomicReference<T>, T> accumulator() {
       return (r,e)->{
         final boolean success = r.compareAndSet(null, e);
         verify(
           success,
           ()->format(
             "Stream has more than one element. Previous element: {}, actual element: {}.",
             r.get(), e
           )
         );
       };
    }
    @Override
    public Set<Characteristics> characteristics() {
      return EnumSet.of(Characteristics.UNORDERED);
    }
    @Override
    public BinaryOperator<AtomicReference<T>> combiner() {
      return (r1,r2)->{
        final T v1 = r1.get();
        final T v2 = r2.get();
        verify(v1==null||v2==null);
        if(v2!=null) r1.set(v2);
        return r1;
      };
    }
    @Override
    public Supplier<AtomicReference<T>> supplier() {
      return AtomicReference::new;
    }
  }

  public static abstract class SortedSetCollector<T,R> implements Collector<T, NavigableSet<T>, R>{
    @Override
    public final BiConsumer<NavigableSet<T>, T> accumulator() {
      return (s,e)->s.add(e);
    }
    @Override
    public Set<Characteristics> characteristics() {
      return EnumSet.of(Characteristics.UNORDERED);
    }
    @Override
    public final BinaryOperator<NavigableSet<T>> combiner() {
      return (s1,s2)->{s1.addAll(s2);return s1;};
    }
    @Override
    public final Supplier<NavigableSet<T>> supplier() {
      return TreeSet::new;
    }
  }

  public static <T> Iterable<T> asIterable(final Supplier<Iterator<T>> iteratorSuppplier){
    return () -> iteratorSuppplier.get();
  }


  public static <T> Opt<T> atMostOne(final Iterable<? extends T> collection){
    return atMostOne(collection, ()->"Collection has more than one element.");
  }


  public static <T> Opt<T> atMostOne(
    final Iterable<? extends T> collection, final Supplier<String> moreThanOneMessage
  ){
    return atMostOne(collection.iterator(), moreThanOneMessage);
  }

  public static <T> Opt<T> atMostOne(
    final Iterator<? extends T> iterator, final Supplier<String> moreThanOneMessage
  ){
    if(!iterator.hasNext()) return Opt.empty();
    else{
      final Opt<T> result = Opt.of(iterator.next());
      verify(!iterator.hasNext(), moreThanOneMessage);
      return result;
    }
  }

  @SafeVarargs
  public static <T> List<T> concat(final Collection<? extends T>... collections){
    Stream<T> s = Stream.empty();
    for(final Collection<? extends T> c: collections){
      s = Stream.concat(s, c.stream());
    }
    return Collections.unmodifiableList(s.collect(toList()));
  }

  public static <K extends Comparable<? super K>,V> ISortedMap<K,V> createIndex(
    final Stream<? extends V> elements, final Function<? super V,? extends K> key
  ){
    return elements.collect(ICollections.toISortedMap(key, v->v));
  }

  public static <T> Opt<T> filter(final Opt<T> optional, final Predicate<? super T> predicate){
    if(optional.isPresent()) {
      if(predicate.test(optional.get())) return optional;
      else return Opt.empty();
    }
    else return optional;
  }

  public static <T> Function<T,Opt<T>> filter(final Predicate<T> predicate){
    return e->predicate.test(e) ? Opt.of(e) : Opt.empty();
  }

  public static <T> OptionalLong findFirstDifference(
    final IList<? extends Stream<? extends T>> streams
  ){
    try{
      return findFirstInMultipleIterators(
        streams.stream().map(Stream::iterator).collect(toIList())
      );
    }
    finally{
      streams.forEach(Stream::close);
    }
  }

  private static <T> OptionalLong findFirstInMultipleIterators(
    final IList<? extends Iterator<? extends T>> iterators
  ){
    final long equal = -1L;
    final long finished = -2L;
    final long result = LongStream.iterate(0, l->l+1L)
      .map(i->{
        final ISet<Optional<T>> different =
          iterators.stream()
          .map(it->it.hasNext() ? Optional.<T>of(it.next()) : Optional.<T>empty())
          .distinct()
          .limit(2L)
          .collect(toISet())
        ;
        if(different.size()>1) return i;
        else if(different.size()==1 ? different.single().isPresent() : false){
          return equal;
        }
        else return finished;
      })
      .filter(i->i!=equal)
      .findFirst().getAsLong()
    ;
    return result == finished ? OptionalLong.empty() : OptionalLong.of(result);
  }

  public static <V> V get(final Map<?,? extends V> map, final Object key){
    final V result = map.get(key);
    if(result==null) {
      throw new NoSuchElementException(format("Map contains no element with key {}.", key));
    }
    return result;
  }

  public static <K,V> V putAbsent(final Map<K,? super V> map, final K key, final V value){
    if(map.putIfAbsent(key, value) != null) {
      throw new IllegalArgumentException(format("Map already contains an element with key {}.", key));
    }
    return value;
  }

  public static IntStream intRange(final int startInclusive, final int endExclusive){
    final int size = endExclusive - startInclusive;
    if(size>=1) return IntStream.range(startInclusive, endExclusive);
    else return IntStream.range(0, size).map(i->startInclusive-i);
  }

  public static IntStream intStream(final int endExclusive){
    return intRange(0, endExclusive);
  }

  public static IntStream intStream(final List<?> list){
    return intStream(list.size());
  }

  public static boolean listEquals(final List<?> list1, final Object o){
    final boolean result;
    if (list1 == o) result = true;
    else if (!(o instanceof List)) result = false;
    else{
      final ListIterator<?> e1 = list1.listIterator();
      final ListIterator<?> e2 = ((List<?>) o).listIterator();
      boolean differenceFound = false;
      while (e1.hasNext() && e2.hasNext() && !differenceFound) {
        final Object o1 = e1.next();
        final Object o2 = e2.next();
        if (!(o1==null ? o2==null : o1.equals(o2))) differenceFound = true;
      }
      result = !differenceFound && !e1.hasNext() && !e2.hasNext();
    }
    return result;
  }

  public static int listHashCode(final Iterable<?> list){
    int hashCode = 1;
    for(final Object e: list) hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
    return hashCode;
  }

  public static LongStream longStream(final long endExclusive){
    return LongStream.range(0L, endExclusive);
  }


  public static <S,T> Iterable<T> mapIterable(
    final Iterable<? extends S> delegate, final Function<? super S, ? extends T> mapping
  ){
    return () -> mapIterator(delegate.iterator(), mapping);
  }

  public static <S,T> Iterator<T> mapIterator(
    final Iterator<? extends S> delegate, final Function<? super S, ? extends T> mapping
  ){
    return new Iterator<>(){
      @Override
      public boolean hasNext() {return delegate.hasNext();}
      @Override
      public T next() {return mapping.apply(delegate.next());}
    };
  }


  public static final <A,B> Either<A,B> newThat(final B b){
    return EitherImp.newThat(b);
  }

  public static final <A,B> Either<A,B> newThis(final A a){
    return EitherImp.newThis(a);
  }

  @Deprecated
  public static final boolean optIs(final Optional<?> optional, final Object obj) {
    return optional.map(v->v.equals(obj)).orElse(false);
  }

  public static <K,V> Pair<K,V> pair(final K key, final V value){
    return new Pair<>(key, value);
  }

  public static final <N> XStream<N> recursiveStream(
    final N root, final Function<N,? extends Stream<? extends N>> children
  ){
    return XStream.of(root).concat(children.apply(root).flatMap(c->recursiveStream(c,children)));
  }


  /**
   * Use {@link ICollection#single}.
   */
  @Deprecated
  public static <T> T single(final ICollection<? extends T> collection){
    return collection.single();
  }


  public static <T> T single(final Iterable<? extends T> collection){
    return single(collection, (n)->n==0?"No element.":"Multiple elements.");
  }

  public static <T> T single(final Iterable<? extends T> collection, final Function<Integer,String> message){
    return single(collection.iterator(), message);
  }

  public static <T> T single(final Iterator<? extends T> it){
    return single(it, i->i==0?"No element.":"Multiple elements.");
  }

  public static <T> T single(final Iterator<? extends T> iterator, final Function<Integer,String> message){
    verify(iterator.hasNext(), ()->message.apply(0));
    final T result = notNull(iterator.next(), ()->message.apply(0));
    verify(!iterator.hasNext(), ()->message.apply(2));
    return result;
  }

  public static <T> T single(final T[] array){
    final int size = array.length;
    verify(size!=0, "No element.");
    verify(size<2, size + " elements.");
    return notNull(array[0]);
  }

  public static <T> XStream<T> stream(final Iterable<T> iterable){
    return XStream.fromSpliterator(iterable.spliterator());
  }

  public static <T> XStream<T> stream(final Iterator<? extends T> iterator){
    return XStream.fromIterator(iterator);
  }

  public static <T> XStream<T> stream(final Spliterator<T> spliterator){
    return XStream.fromSpliterator(spliterator);
  }

  public static <T> XStream<T> stream(final Optional<? extends T> optional){
    return XStream.fromOptional(optional);
  }

  public static <T> XStream<T> stream(final T[] array){
    return XStream.of(array);
  }

  public static <T, K, V> Collector<T, ?, Map<K,V>>
  toMapOpt(
    final Function<? super T, ? extends K> keyMapper,
    final Function<? super T, Optional<? extends V>> valueMapper
  ) {
    return new Collector<T,Map<K,V>, Map<K,V>>(){
      @Override
      public BiConsumer<Map<K, V>, T> accumulator() {
         return (b,t)->{
           final Optional<? extends V> optValue = valueMapper.apply(
             notNull(t, ()->"Null element found in the stream.")
           );
           if(optValue.isPresent()){
             b.put(
               keyMapper.apply(t),
               optValue.get()
             );
           }
         };
      }
      @Override
      public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.UNORDERED);
      }
      @Override
      public BinaryOperator<Map<K, V>> combiner() {
        return (b1,b2)->{
          b1.putAll(b2);
          return b1;
        };
      }
      @Override
      public Function<Map<K, V>, Map<K, V>> finisher() {
        return Collections::unmodifiableMap;
      }
      @Override
      public Supplier<Map<K, V>> supplier() {
        return HashMap::new;
      }
    };
  }

  public static <T> Collector<T,?,Opt<T>> toOpt(){
    return new OptCollector<>(){
      @Override
      public Function<AtomicReference<T>, Opt<T>> finisher() {
        return r->Opt.ofNullable(r.get());
      }
    };
  }

  public static <T> Optional<T> toOptional(final Opt<? extends T> opt){
    return opt.isPresent() ? Optional.of(opt.get()) : Optional.empty();
  }

  public static <T> Collector<T,?,Stream<T>> toOptionalStream(){
    return new OptCollector<>(){
      @Override
      public Function<AtomicReference<T>, Stream<T>> finisher() {
        return r->{final T v=r.get(); return v==null?Stream.empty():Stream.of(v);};
      }
    };
  }


  public static <T> Collector<T,?,T> toSingle(){
    return toSingle(()->"Empty stream.");
  }

  public static <T> Collector<T,?,T> toSingle(final Supplier<String> msg){
    return new OptCollector<>(){
      @Override
      public Function<AtomicReference<T>, T> finisher() {
        return r->notNull(r.get(), msg);
      }
    };
  }

  public static <T> Collector<T,?,Stream<T>> toSingleStream(){
    return new OptCollector<>(){
      @Override
      public Function<AtomicReference<T>, Stream<T>> finisher() {
        return r->Stream.of(notNull(r.get(), ()->"Empty stream."));
      }
    };
  }

  public static final <K extends Comparable<? super K>,V,E> Collector<E,?,NavigableMap<K,V>> toSortedMap(
    final Function<E,K> keyMapper, final Function<E,V> valueMapper
  ) {
    return Collectors.toMap(
      keyMapper,
      valueMapper,
      (v1,v2) ->{ throw new RuntimeException(String.format("Duplicate key for values %s and %s", v1, v2));},
      TreeMap::new
    );
  }

  public static final <T extends Comparable<? super T>> Collector<T, ?, NavigableSet<T>> toSortedSet(){
    return new SortedSetCollector<>(){
      @Override
      public Function<NavigableSet<T>, NavigableSet<T>> finisher() {
        return Function.identity();
      }
    };
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <V> Opt<V> tryGet(final Map<?,? extends V> map, final Object key){
    return IMap.class.isInstance(map) ? ((IMap)map).tryGet(key) : Opt.ofNullable(map.get(key));
  }

  public static String collectToString(final IntStream codepoints){
    return codepoints.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
  }


  public static <E> Opt<E> tryGetFirst(final SortedSet<E> sortedSet) {
    return sortedSet.isEmpty() ? Opt.empty() : Opt.of(sortedSet.first());
  }

  public static <K> Opt<K> tryGetFirstKey(final SortedMap<K,?> sortedMap) {
    return sortedMap.isEmpty() ? Opt.empty() : Opt.of(sortedMap.firstKey());
  }

  public static final <I,O> Function<I,Stream<O>> filter(final Class<O> clazz){
    return i -> clazz.isInstance(i) ? Stream.of(clazz.cast(i)) : Stream.empty();
  }


  public static final <T,A,R> Collector<T,A,R> limitingCollector(final Collector<T,A,R> downstream, final long limit){
    final AtomicLong l = new AtomicLong(limit);
    final BiConsumer<A, T> acc = downstream.accumulator();
    return new Collector<>(){
      @Override
      public Set<Characteristics> characteristics() {
        return downstream.characteristics();
      }
      @Override
      public Supplier<A> supplier() {
        return downstream.supplier();
      }
      @Override
      public BiConsumer<A, T> accumulator() {
        return (a,t)->{if(l.decrementAndGet()>=0L) acc.accept(a, t);};
      }
      @Override
      public BinaryOperator<A> combiner() {
        return downstream.combiner();
      }
      @Override
      public Function<A, R> finisher() {
        return downstream.finisher();
      }
    };
  }

  private static final Set<Characteristics> LAST_COLLECTOR_CHARACTERISTICS =
      Collections.unmodifiableSet(EnumSet.of(CONCURRENT))
  ;

  public static final <T> Collector<T,?,Opt<T>> collectLast(){
    return new Collector<T, AtomicReference<T>, Opt<T>>(){
      @Override
      public Set<Characteristics> characteristics() {
        return LAST_COLLECTOR_CHARACTERISTICS;
      }
      @Override
      public Supplier<AtomicReference<T>> supplier() {
        return AtomicReference::new;
      }
      @Override
      public BiConsumer<AtomicReference<T>, T> accumulator() {
        return (r,t)->r.set(t);
      }
      @Override
      public BinaryOperator<AtomicReference<T>> combiner() {
        return (l,r)->r;
      }
      @Override
      public Function<AtomicReference<T>, Opt<T>> finisher() {
        return r->Opt.ofNullable(r.get());
      }
    };
  }


  public static boolean hasDuplicates(final Collection<?> collection) {
    return collection.size() != new HashSet<>(collection).size();
  }

}
