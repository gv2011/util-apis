package com.github.gv2011.util;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.CollectionUtils.toSingle;
import static com.github.gv2011.util.ex.Exceptions.call;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.gv2011.util.ex.ThrowingFunction;
import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;


public interface XStream<E> extends Stream<E>, AutoCloseableNt{

  XStream<E> concat(final Stream<? extends E> other);

  /**
   * Use {@link #tryFindFirst} instead.
   */
  @Override
  @Deprecated
  Optional<E> findFirst();

  /**
   * Use {@link #tryFindAny} instead.
   */
  @Override
  @Deprecated
  Optional<E> findAny();

  default Opt<E> tryFindFirst(final Predicate<? super E> predicate) {
    return filter(predicate).tryFindFirst();
  }

  default Opt<E> tryFindAny(final Predicate<? super E> predicate) {
    return filter(predicate).tryFindAny();
  }

  default Opt<E> tryFindFirst(){
    return Opt.ofOptional(findFirst());
  }

  default Opt<E> tryFindAny(){
    return Opt.ofOptional(findAny());
  }

  default E findSingle(){
    return collect(toSingle());
  }

  default Opt<E> toOpt(){
    return collect(CollectionUtils.toOpt());
  }

  default IList<E> toIList(){
    return collect(ICollections.toIList());
  }

  default ISet<E> toISet(){
    return collect(ICollections.toISet());
  }

  default E findSingle(final Predicate<? super E> predicate){
    return filter(predicate).collect(toSingle());
  }

  default Opt<E> toOpt(final Predicate<? super E> predicate){
    return filter(predicate).collect(CollectionUtils.toOpt());
  }

  @Override
  XStream<E> filter(Predicate<? super E> predicate);

  @Override
  XStream<E> sorted(Comparator<? super E> comparator);

  @Override
  XStream<E> sorted();


  @Override
  <R> XStream<R> map(Function<? super E, ? extends R> mapper);

  @Override
  <R> XStream<R> flatMap(Function<? super E, ? extends Stream<? extends R>> mapper);

  default <R> XStream<R> mapThrowing(final ThrowingFunction<? super E, ? extends R> mapper){
    return map(e->call(()->mapper.apply(e)));
  }

  default <R> XStream<Pair<E,R>> enrich(final Function<? super E, ? extends R> mapper){
    return map(e->pair(e, mapper.apply(e)));
  }

  <T> XStream<T> filter(final Class<T> clazz);

  <R> XStream<R> flatOpt(final Function<? super E, ? extends Opt<? extends R>> mapper);

  @Override
  default void close() {}

  public static<T> XStream<T> of(final T t) {
      return xStream(Stream.of(t));
  }

  public static<T> XStream<T> parallelStreamOf(final T t) {
      return xStream(Stream.of(t));
  }

  @SafeVarargs
  public static<T> XStream<T> of(final T... values) {
    return xStream(Arrays.stream(values));
}

  public static<T> XStream<T> ofArray(final T[] values) {
    return xStream(Arrays.stream(values));
}

  public static <E> XStream<E> pStream(final Stream<E> s){
    return ICollections.pStream(s);
  }

  public static <E> XStream<E> xStream(final Stream<E> s){
    return ICollections.xStream(s);
  }

  public static <E> XStream<E> empty(){
    return ICollections.xStream(Stream.empty());
  }

  public static <E> XStream<E> fromOptional(final Optional<? extends E> optional){
    return optional.map(e->XStream.of((E)e)).orElseGet(XStream::empty);
  }

  public static <E> XStream<E> stream(
    final Supplier<? extends Spliterator<E>> supplier,
    final int characteristics,
    final boolean parallel
  ) {
    return xStream(StreamSupport.stream(supplier, characteristics, parallel));
  }

  public static <E> XStream<E> stream(final Spliterator<E> spliterator, final boolean parallel) {
    return ICollections.xStream(spliterator, parallel);
  }

  public static <E> XStream<E> fromIterator(final Iterator<? extends E> iterator) {
    return ICollections.xStream(
      Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
      false
    );
  }

  public static <T> XStream<T> concat(final Stream<? extends T> a, final Stream<? extends T> b) {
    return xStream(Stream.concat(a, b)
    );
  }
}
