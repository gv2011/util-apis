package com.github.gv2011.util.beans;

public interface Partial<B>{

    boolean isAvailable(Property<?> p);

    //<T> Optional<T> tryGet(Function<B,T> property);

    boolean isAvailable(String propertyName);

    <T> T get(Property<T> p);

}
