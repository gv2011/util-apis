package com.github.gv2011.util.icol;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.icol.ICollections.emptyList;
import static com.github.gv2011.util.icol.ICollections.setFrom;
import static com.github.gv2011.util.icol.ICollections.setOf;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.OptionalInt;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import com.github.gv2011.util.XStream;
import com.github.gv2011.util.ex.ThrowingConsumer;
import com.github.gv2011.util.ex.ThrowingFunction;
import com.github.gv2011.util.ex.ThrowingRunnable;
import com.github.gv2011.util.ex.ThrowingSupplier;

public interface Empty<E> extends Opt<E>{

  @Override
  default XStream<E> stream() {
    return XStream.empty();
  }

  @Override
  default XStream<E> parallelStream() {
    return XStream.empty();
  }

  @Override
  default ISet<E> join(final Collection<? extends E> other) {
    return setFrom(other);
  }

  @Override
  default Single<E> addElement(final E other) {
    return setOf(other);
  }

  @Override
  default Empty<E> intersection(final Collection<?> other) {
    return this;
  }

  @Override
  default Empty<E> asOpt() {
    return this;
  }

  @Override
  default E single() {
    throw new NoSuchElementException();
  }

  @Override
  default E first() {
    throw new NoSuchElementException();
  }

  @Override
  default Empty<E> tryGetFirst() {
    return this;
  }

  @Override
  default boolean containsElement(final E element) {
    return false;
  }

  @Override
  default IList<E> asList() {
    return emptyList();
  }

  @Override
  default int size() {
    return 0;
  }

  @Override
  default boolean contains(final Object o) {
    return false;
  }

  @Override
  default boolean containsAll(final Collection<?> c) {
    return false;
  }

  @Override
  default void forEach(final Consumer<? super E> action) {}

  @Override
  default E get() {
    throw new NoSuchElementException();
  }

  @Override
  default boolean isPresent() {
    return false;
  }

  @Override
  default Empty<E> filter(final Predicate<? super E> predicate) {
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  default <U> Empty<U> map(final ThrowingFunction<? super E, ? extends U> mapper) {
    return (Empty<U>) this;
  }

  @Override
  default Empty<E> ifPresentDo(final ThrowingConsumer<? super E> consumer) {
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  default <U> Empty<U> flatMap(final Function<? super E, ? extends Opt<? extends U>> mapper) {
    return (Empty<U>) this;
  }

  @Override
  @SuppressWarnings("unchecked")
  default Opt<E> or(final Supplier<? extends Opt<? extends E>> supplier) {
    return (Opt<E>) supplier.get();
  }

  @Override
  default E orElse(final E other) {
    return other;
  }

  @Override
  default E orElseGet(final ThrowingSupplier<? extends E> supplier) {
    return supplier.get();
  }

  @Override
  default Nothing orElseDo(final ThrowingRunnable operation) {
    call(operation);
    return (Nothing) this;
  }

  @Override
  default Empty<E> ifEmptyDo(final ThrowingRunnable operation) {
    call(operation);
    return this;
  }

  @Override
  default <X extends Throwable> E orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X {
    throw exceptionSupplier.get();
  }

  @Override
  default boolean isEmpty() {
    return true;
  }

  @Override
  default Spliterator<E> spliterator() {
    return Spliterators.emptySpliterator();
  }

  @Override
  @SuppressWarnings("unchecked")
  default Opt<E> merge(final Opt<? extends E> other) {
    return (Opt<E>) other;
  }

  @Override
  default Empty<E> subtract(final Collection<?> other) {
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  default <T> Empty<T> tryCast(final Class<T> clazz) {
     return (Empty<T>) this;
  }

  @Override
  default OptionalInt mapToInt(final ToIntFunction<? super E> mapping) {
    return OptionalInt.empty();
  }

  @Override
  default OptionalInt flatMapToInt(final Function<? super E, OptionalInt> mapping) {
    return OptionalInt.empty();
  }
}
