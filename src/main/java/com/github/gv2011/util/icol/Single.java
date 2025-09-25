package com.github.gv2011.util.icol;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.empty;
import static com.github.gv2011.util.icol.ICollections.iCollections;
import static com.github.gv2011.util.icol.ICollections.listOf;
import static com.github.gv2011.util.icol.ICollections.nothing;
import static com.github.gv2011.util.icol.ICollections.setOf;

import java.util.Collection;
import java.util.OptionalInt;
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

public interface Single<E> extends Opt<E>{

  @Override
  default XStream<E> stream() {
    return XStream.of(get());
  }

  @Override
  default XStream<E> parallelStream() {
    return XStream.parallelStreamOf(get());
  }

  @Override
  default Single<E> asOpt() {
    return this;
  }

  @Override
  default E single() {
    return get();
  }

  default E value() {
    return get();
  }

  @Override
  default E first() {
    return get();
  }

  @Override
  default Single<E> tryGetFirst() {
    return this;
  }

  @Override
  default boolean containsElement(final E element) {
    return get().equals(element);
  }

  @Override
  default IList<E> asList() {
    return listOf(get());
  }

  @Override
  default int size() {
    return 1;
  }

  @Override
  default boolean contains(final Object o) {
    return get().equals(o);
  }

  @Override
  default boolean containsAll(final Collection<?> c) {
    final E element = get();
    return c.parallelStream().allMatch(e->e.equals(element));
  }

  @Override
  default Opt<E> intersection(final Collection<?> other) {
    return other.contains(get()) ? this : empty();
  }

  @Override
  default void forEach(final Consumer<? super E> action) {
    action.accept(get());
  }

  @Override
  default boolean isPresent() {
    return true;
  }

  @Override
  default Opt<E> filter(final Predicate<? super E> predicate) {
    return predicate.test(get()) ? this : empty();
  }

  @Override
  default <U> Single<U> map(final ThrowingFunction<? super E, ? extends U> mapper) {
    return setOf(mapper.apply(get()));
  }

  @Override
  default Single<E> ifPresentDo(final ThrowingConsumer<? super E> consumer) {
    call(()->consumer.accept(get()));
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  default <U> Opt<U> flatMap(final Function<? super E, ? extends Opt<? extends U>> mapper) {
    return (Opt<U>) mapper.apply(get());
  }

  @Override
  default Single<E> or(final Supplier<? extends Opt<? extends E>> supplier) {
    return this;
  }

  @Override
  default E orElse(final E other) {
    return get();
  }

  @Override
  default E orElseGet(final ThrowingSupplier<? extends E> supplier) {
    return get();
  }

  @Override
  default Nothing orElseDo(final ThrowingRunnable operation) {
    return nothing();
  }

  @Override
  default <X extends Throwable> E orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X {
    return get();
  }

  @Override
  default boolean isEmpty() {
    return false;
  }

  @Override
  default ISet<E> addElement(final E other) {
    return get().equals(other) ? this : iCollections().<E>setBuilder().add(get()).add(other).build();
  }


  @Override
  default Opt<E> merge(final Opt<? extends E> other) {
    if(other.isPresent()) throw new IllegalStateException(
      format("Both optional values are present: ({} and {}).", get(), other.get())
    );
    return this;
  }

  @Override
  default Opt<E> subtract(final Collection<?> other) {
    return other.contains(get()) ? empty() : this;
  }

  @Override
  default <T> Opt<T> tryCast(final Class<T> clazz) {
    return clazz.isInstance(get()) ? setOf(clazz.cast(get())) : empty();
  }

  @Override
  default OptionalInt mapToInt(final ToIntFunction<? super E> mapping) {
    return OptionalInt.of(mapping.applyAsInt(get()));
  }

  @Override
  default OptionalInt flatMapToInt(final Function<? super E, OptionalInt> mapping) {
    return mapping.apply(get());
  }

}
