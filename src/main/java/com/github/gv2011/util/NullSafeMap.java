package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.format;

import java.util.Map;
import java.util.NoSuchElementException;

import com.github.gv2011.util.icol.Opt;

public interface NullSafeMap<K,V> extends Map<K,V>{

  Opt<V> tryGet(Object key);

  Opt<V> tryRemove(Object key);

  /**
   * Returns the value to which the specified key is mapped,
   * or throws {@link NoSuchElementException} if this map contains no mapping for the key.
   *
   * This is a major difference to the behaviour of {@Map}, which is needed to avoid the
   * usage of {@code null}.
   *
   * {@link #tryGet} can be used if the presence of a mapping is not known.
   *
   * <p>More formally, if this map contains a mapping from a key
   * {@code k} to a value {@code v} such that key.equals(k))}, then this method returns
   * {@code v}; otherwise it throws {@link NoSuchElementException}.  (There can be at most one such mapping.)
   *
   * <p>This map must not contain {@code null} values.
   *
   * @param key the key whose associated value is to be returned. The behaviour is unspecified if key is {@code null}.
   * @return the value to which the specified key is mapped
   * @throws NoSuchElementException if the key is not mapped to any value
   *
   * @see java.util.Map#get(java.lang.Object)
   */
  @Override
  default V get(final Object key) {
    return tryGet(key).orElseThrow(()->new NoSuchElementException(format("No entry for key {}.", key)));
  }

  @Override
  default V remove(final Object key) {
    return tryRemove(key).orElseThrow(()->new NoSuchElementException(format("No entry for key {}.", key)));
  }


}
