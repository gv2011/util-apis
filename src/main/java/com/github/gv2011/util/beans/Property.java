package com.github.gv2011.util.beans;

import com.github.gv2011.util.icol.Opt;

public interface Property<V> {

    String name();

    Type<V> type();

    Opt<V> defaultValue();

    Opt<V> fixedValue();

    boolean isKey();

}
