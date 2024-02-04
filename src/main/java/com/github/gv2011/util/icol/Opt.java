package com.github.gv2011.util.icol;


import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.nothing;

import java.util.Collection;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.StreamAccess;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.ex.ThrowingConsumer;
import com.github.gv2011.util.ex.ThrowingFunction;
import com.github.gv2011.util.ex.ThrowingRunnable;
import com.github.gv2011.util.ex.ThrowingSupplier;

public interface Opt<E> extends ISet<E>, StreamAccess<E, Opt<E>>, Constant<E>{

  public static <E> Single<E> of(final E element){
    return ICollections.single(element);
  }

  public static <E> Opt<E> ofOptional(final Optional<? extends E> optional){
    return ICollections.ofOptional(optional);
  }

  public static <E> Opt<E> ofNullable(@Nullable final E element){
    return ICollections.ofNullable(element);
  }

  public static <E> Opt<E> empty(){
    return ICollections.empty();
  }

  @Override
  E get();

  boolean isPresent();

  @Override
  Opt<E> filter(final Predicate<? super E> predicate);

  <U> Opt<U> map(final ThrowingFunction<? super E, ? extends U> mapper);

  default Opt<E> ifPresentDo(final ThrowingConsumer<? super E> consumer){
    if(isPresent()) call(()->consumer.accept(get()));
    return this;
  }

  default void ifPresentDoOrElse(
    final ThrowingConsumer<? super E> consumer,
    final ThrowingRunnable defaultAction
  ){
    if(isPresent()) call(()->consumer.accept(get()));
    else defaultAction.run();
  }

  <U> Opt<U> flatMap(final Function<? super E, ? extends Opt<? extends U>> mapper);

  Opt<E> or(final Supplier<? extends Opt<? extends E>> supplier);

  E orElse(final E other);

  E orElseGet(final ThrowingSupplier<? extends E> supplier);

  default Nothing orElseDo(final ThrowingRunnable operation){
    if(isEmpty()) call(operation);
    return nothing();
  }

  default Opt<E> ifEmptyDo(final ThrowingRunnable operation){
    if(isEmpty()) call(operation);
    return this;
  }

  <X extends Throwable> E orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X;

  @Override
  default boolean add(final E e) {
    throw new UnsupportedOperationException();
  }

  @Override
  default boolean remove(final Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  default boolean addAll(final Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  default boolean isEmpty() {
    return !isPresent();
  }

  @Override
  default Spliterator<E> spliterator() {
    if(isPresent()) return Spliterators.spliterator(new Object[]{get()}, 0);
    else return Spliterators.emptySpliterator();
  }

  @Override
  default boolean removeAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  default void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  default boolean retainAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unchecked")
  default Opt<E> merge(final Opt<? extends E> other){
    if(!isPresent()) return (Opt<E>) other;
    else if(!other.isPresent()) return this;
    else throw new IllegalStateException(
      format("Both optional values are present: ({} and {}).", this.get(), other.get())
    );
  }

  @Override
  default Object[] toArray() {
    Object[] result;
    if(isPresent()) {
      result = new Object[1];
      result[0] = get();
    }
    else result = new Object[0];
    return result;
  }

  @Override
  public Opt<E> subtract(final Collection<?> other);

  default <T> Opt<T> tryCast(final Class<T> clazz){
    return flatMap(o->clazz.isInstance(o) ? of(clazz.cast(o)) : empty());
  }

  default OptionalInt mapToInt(final ToIntFunction<? super E> mapping){
    return isPresent() ? OptionalInt.of(mapping.applyAsInt(get())) : OptionalInt.empty();
  }

  default OptionalInt flatMapToInt(final Function<? super E, OptionalInt> mapping){
    return isPresent() ? mapping.apply(get()) : OptionalInt.empty();
  }

}
