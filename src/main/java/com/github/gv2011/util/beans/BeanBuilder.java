package com.github.gv2011.util.beans;

import java.util.function.Function;

import com.github.gv2011.util.icol.ICollection;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.tstr.TypedString;

public interface BeanBuilder<T> {

    T build();

    <V> void set(Property<V> p, V value);

    <V> Setter<T,V> set(Function<T,V> method);

    <V> Setter<T,V> setOpt(Function<T,Opt<V>> method);

    <V extends TypedString<V>> Setter<T,String> setTStr(final Function<T,V> method);

    default <V extends TypedString<V>> BeanBuilder<T> set(final Function<T,V> method, final String s){
      return setTStr(method).to(s);
    }

    BeanBuilder<T> setAll(T bean);

    BeanBuilder<T> setProperties(T bean, ICollection<Function<T,?>> methods);

    public interface Setter<T,V> {
      BeanBuilder<T> to(V value);
     }

}
