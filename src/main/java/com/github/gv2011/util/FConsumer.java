package com.github.gv2011.util;

import java.util.function.Function;

import com.github.gv2011.util.icol.Nothing;

@FunctionalInterface
public interface FConsumer<T> extends Function<T,Nothing>{

}
