package com.github.gv2011.util;

import com.github.gv2011.util.icol.Opt;

public interface Store<T>{
    Opt<T> tryRead();
    void store(T element);
}
