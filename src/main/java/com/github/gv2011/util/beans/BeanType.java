package com.github.gv2011.util.beans;

import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.Opt;

public interface BeanType<T> extends Type<T>{

    ExtendedBeanBuilder<T> createBuilder();

    Partial<T> emptyPartial();

    ISortedMap<String,? extends Property<?>> properties();

    <V> V get(T bean, Property<V> property);

    int hashCode(T bean);

    boolean equal(T bean, Object other);

    String toString(T bean);

    boolean isKeyBean();

    Opt<Property<?>> keyProperty();

    Opt<Ref<?,T>> ref(T bean);

}
