package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.icol.ICollection;

public final class Verify {

  private Verify(){staticClass();}

  public static void verify(final boolean expr) {
    if(!expr) throw new IllegalStateException("Verify failed.");
  }
  public static <T> T verify(final T arg, final Predicate<? super T> predicate) {
    return verify(arg, predicate, a->format("Unexpected: {}", a));
  }

  public static <T> T verify(final T arg, final Predicate<? super T> predicate, final Function<? super T,String> msg) {
    if(!predicate.test(arg)){
      throw new IllegalStateException(msg.apply(arg));
    }
    return arg;
  }

  public static <T> Function<T,T> verifier(final Predicate<? super T> predicate) {
    return arg->verify(arg, predicate);
  }

  public static <T> Function<T,T> verifier(final Predicate<? super T> predicate, final Function<? super T,String> msg) {
    return arg->verify(arg, predicate, msg);
  }

  public static void verify(final boolean expr, final Supplier<String> msg) {
    if(!expr) throw new IllegalStateException(msg.get());
  }

  public static void verify(final boolean expr, final String pattern, final Object... params) {
    verify(expr, ()->format(pattern, params));
  }

  public static <T> T verifyEqual(final @Nullable T actual, final @Nullable T expected) {
    if(!Objects.equals(actual, expected)){
      throw new IllegalStateException(format("Expected: {}, actual: {}.", expected, actual));
    }
    return actual;
  }

  public static <T> T verifyEqualsResult(final @Nullable T actual, final Function<T,T> expectedProducingFunction) {
    notNull(actual);
    return verifyEqual(actual, expectedProducingFunction.apply(actual));
  }

  public static <T> T verifyEqual(final T actual, final T expected, final BiFunction<T,T,String> msg) {
    if(!actual.equals(expected)){
      throw new IllegalStateException(msg.apply(expected, actual));
    }
    return actual;
  }

  public static <T> T verifyIn(
    final T actual,
    final ICollection<T> expectedValues,
    final BiFunction<ICollection<T>,T,String> msg
  ) {
    if(!expectedValues.contains(actual)){
      throw new IllegalStateException(msg.apply(expectedValues, actual));
    }
    return actual;
  }

  public static <T> UnaryOperator<T> check(final Predicate<T> predicate) {
    return check(predicate, (Function<T,String>)String::valueOf);
  }

//  TODO remove
//  @Deprecated //use check
//  public static <T> UnaryOperator<T> verify(final Predicate<T> predicate) {
//    return check(predicate);
//  }

  public static <T> UnaryOperator<T> check(final Predicate<T> predicate, final Function<? super T,String> msg) {
    return e->{return verify(e, predicate, msg);};
  }

  public static <T> T notNull(final T arg){
    return notNull(arg, ()->"Null value.");
  }

  public static <T> T notNull(final T arg, final Supplier<String> msg){
    return verify(arg, a->a!=null, a->msg.get());
  }

  public static Runnable noop(){return ()->{};}

  public static <T> Optional<T> tryCast(final Object obj, final Class<? extends T> clazz){
    if(clazz.isInstance(obj)) return Optional.of((T)clazz.cast(obj));
    else return Optional.empty();
  }

}
