package com.github.gv2011.util.cache;

import static com.github.gv2011.util.CollectionUtils.pair;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;

import com.github.gv2011.util.FConsumer;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.icol.Opt;

final class SoftIndexImp<K,V> implements SoftIndex<K,V>{

@SuppressWarnings("unused")
private static final Logger LOG = getLogger(SoftIndexImp.class);

  private final Object lock = new Object();

  private final Function<K, Opt<? extends V>> function;
  private final Map<K,Opt<V>> index = new HashMap<>();

  private final FConsumer<Pair<K,Opt<V>>> addedListener;

  SoftIndexImp(
    final Function<K, Opt<? extends V>> function,
    final FConsumer<Pair<K,Opt<V>>> addedListener
  ) {
    this.function = function;
    this.addedListener = addedListener;
  }


  @Override
  public Opt<V> tryGet(final K key) {
    synchronized(lock) {
      Opt<V> result;
      final @Nullable Opt<V> opt = index.get(key);
      if(opt!=null) return result = opt;
      else {
        result = function.apply(key).map(v->v);
        index.put(key, result);
        addedListener.apply(pair(key, result));
      }
      return result;
    }
  }

  @Override
  public Opt<Opt<V>> getIfPresent(final K key) {
    synchronized(lock) {
      return Opt.ofNullable(index.get(key));
    }
  }

  @Override
  public V get(final K key) {
    return tryGet(key).get();
  }

}
