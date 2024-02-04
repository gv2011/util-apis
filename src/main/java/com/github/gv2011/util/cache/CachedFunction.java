package com.github.gv2011.util.cache;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

final class CachedFunction<K, V> implements Function<K, V> {

  private volatile SoftReference<Map<K,V>> cache = new SoftReference<>(null);
  private final Function<K,V> function;

  CachedFunction(final Function<K, V> function) {
    this.function = function;
  }

  @Override
  public V apply(final K key) {
    return
      Optional.ofNullable(cache.get())
      .orElseGet(()->{
        final ConcurrentHashMap<K, V> m = new ConcurrentHashMap<>();
        cache = new SoftReference<>(m);
        return m;
      })
      .computeIfAbsent(key, function)
    ;
  }

}
