package com.github.gv2011.util.cache;

import static com.github.gv2011.util.CollectionUtils.tryGet;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.icol.ICollections.ofNullable;

import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.github.gv2011.util.icol.Opt;

final class CachingProxy<T> implements InvocationHandler {

  private final T delegate;
  private final ThreadLocal<SoftReference<Map<String,Object>>> cache = new ThreadLocal<>();

  CachingProxy(final T delegate) {
    this.delegate = delegate;
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    if(isCached(method)){
      return tryGetCached(method.getName())
        .orElseGet(()->{
          final Object fromDelegate = method.invoke(delegate, args);
          store(method.getName(), fromDelegate);
          return fromDelegate;
        })
      ;
    }
    else return method.invoke(delegate, args);
  }

  private boolean isCached(final Method method) {
    return method.getParameterCount()==0 && !method.getReturnType().equals(Stream.class);
  }

  private void store(final String name, final Object fromDelegate) {
    final Opt<Object> previous = ofNullable(getMap().put(name, fromDelegate));
    previous.ifPresentDo(p->verifyEqual(fromDelegate, p));
  }

  private Map<String,Object> getMap() {
    return ofNullable(cache.get())
      .flatMap(sr->ofNullable(sr.get()))
      .orElseGet(()->{
        final Map<String, Object> m = new HashMap<>();
        cache.set(new SoftReference<>(m));
        return m;
      })
    ;
  }

  private Opt<Object> tryGetCached(final String name) {
    return tryGet(getMap(), name);
  }

}
