package com.github.gv2011.util.ex;

import java.util.function.Function;

public interface Throwing<T,R> {

  Function<T,R> asFunction();

}
