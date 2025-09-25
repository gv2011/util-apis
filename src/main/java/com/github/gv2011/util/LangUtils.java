package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.util.function.Function;
import java.util.function.Predicate;

import com.github.gv2011.util.icol.Opt;

public final class LangUtils {

  private LangUtils(){staticClass();}

  public static <T,R> Opt<R> ifMeetsDo(final T value, final Predicate<T> predicate, final Function<T,R> operation){
    return predicate.test(value) ? Opt.of(operation.apply(value)) : Opt.empty();
  }

}
