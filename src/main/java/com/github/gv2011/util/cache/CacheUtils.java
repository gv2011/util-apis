package com.github.gv2011.util.cache;

import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.icol.ICollections.nothing;

import java.lang.ref.SoftReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.gv2011.util.FConsumer;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.icol.Opt;

public final class CacheUtils {

  private CacheUtils(){staticClass();}

  public static <T> Supplier<T> cache(final Supplier<T> supplier){
    return new SoftRefCache<>(supplier)::get;
  }

  public static <K,V> Function<K,V> cachedFunction(final Function<K,V> f){
    return new CachedFunction<K,V>(f);
  }

  public static <K,V> SoftIndex<K,V> softIndex(final Function<K,Opt<? extends V>> constantFunction){
    return new SoftIndexImp<>(constantFunction, p->nothing());
  }

  public static <K,V> SoftIndex<K,V> softIndex(
    final Function<K,Opt<? extends V>> constantFunction, final FConsumer<Pair<K,Opt<V>>> addedListener
  ){
    return new SoftIndexImp<>(constantFunction, addedListener);
  }

  public static <C> C readFromReference(
    final @Nullable SoftReference<C> ref,
    final Consumer<SoftReference<C>> refSetter,
    final Supplier<C> constructor
  ){
    @Nullable C cache = ref!=null ? ref.get() : null;
    if(cache == null){
      cache = constructor.get();
      refSetter.accept(new SoftReference<>(cache));
    }
    return cache;
  }

  public static <T> T lazy(
      @Nullable T value,
      final Consumer<T> store,
      final Supplier<T> calculator) {
    if(value==null){
      value = calculator.get();
      store.accept(value);
    }
    return value;
  }
}
