package com.github.gv2011.util.icol;

import java.util.Map.Entry;

public interface IEntry<K,V> extends Entry<K,V>{

  public static final String KEY_VALUE_SEPARATOR = "=";

  @SuppressWarnings("unchecked")
  static <K,V> IEntry<K,V> cast(final IEntry<? extends K, ? extends V> entry){return (IEntry<K, V>) entry;}

  @Override
  default V setValue(final V value) {
    throw new UnsupportedOperationException();
  }

}
