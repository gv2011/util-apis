package com.github.gv2011.util;

import java.util.function.Predicate;

public interface StreamAccess<E, S extends StreamAccess<E,S>> {

  S filter(Predicate<? super E> predicate);

}
