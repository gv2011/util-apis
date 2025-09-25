package com.github.gv2011.util.icol;

import java.util.Map.Entry;

public interface ListEntry<T> extends Entry<Integer,T>{

  default int index(){
    return getKey().intValue();
  }

  Opt<ListEntry<T>> tryGetPrevious();

  Opt<ListEntry<T>> tryGetNext();

}
