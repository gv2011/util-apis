package com.github.gv2011.util;

import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.lang.ref.SoftReference;

import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.ex.ThrowingConsumer;
import com.github.gv2011.util.ex.ThrowingSupplier;
import com.github.gv2011.util.icol.Opt;

@com.github.gv2011.util.ann.ThreadSafe
public final class Constants{

  private Constants(){staticClass();}

  /**
   * The value of the constant is lazily retrieved from the supplier. It is cached in a soft reference, so the
   * supplier may be called multiple times. The supplier must guarantee to return always the same value.
   */
  public static final <T> CachedConstant<T> softRefConstant(final Constant<? extends T> supplier){
    return new SoftRefConstant<>(supplier);
  }

  /**
   * The value of the constant must be set before it is retrieved.
   */
  public static final <T> CachedConstant<T> cachedConstant(){
    return new ConstantImp<>(()->{throw new IllegalStateException("Value has not been set.");});
  }

  /**
   * The value of the constant is lazily retrieved from the supplier. It is cached forever, so the
   * supplier is called at most once.
   */
  public static final <T> CachedConstant<T> cachedConstant(final ThrowingSupplier<? extends T> supplier){
    return new ConstantImp<T>(supplier);
  }

  /**
   * The value of the constant is lazily retrieved from the supplier. It is cached forever, so the
   * supplier is called at most . The supplier must guarantee to return always the same value.
   */
  public static final <T extends AutoCloseable> CloseableCachedConstant<T> closeableCachedConstant(
      final ThrowingSupplier<? extends T> supplier
    ){
      return closeableCachedConstant(supplier, AutoCloseable::close);
    }

  public static final <T> CloseableCachedConstant<T> closeableCachedConstant(
      final ThrowingSupplier<? extends T> supplier, final ThrowingConsumer<? super T> closer
    ){
      return new CloseableConstantImp<>(supplier, closer);
    }

  /**
   * Cache algorithm that makes use of the constant character by unsynchronized read access to a instance variable.
   * (no "out-of-the-air values" implies: if the value is not null, it is correct.)
   */
  private static abstract class AbstractCachedConstant<E> implements CachedConstant<E>{

    protected final Object lock = new Object();

    @Override
    public final E get() {
      @Nullable E result = getIntern();
      if(result==null){
        synchronized(lock){
          result = getIntern();
          if(result==null){
            result = retrieveValue();
            if(result==null) throw new NullPointerException("Retrieved null value.");
            setIntern(result);
          }
        }
      }
      return result;
    }

    @Override
    public Opt<E> tryGet() {
      @Nullable E result = getIntern();
      if(result==null){
        synchronized(lock){
          result = getIntern();
        }
      }
      return Opt.ofNullable(result);
    }

    @Override
    public final void set(final E value) {
      final E v = ConcurrencyUtils.safePublish(()->value);
      synchronized(lock){
        final @Nullable E current = getIntern();
        if(current==null){
          setIntern(v);
        }else{
          verifyEqual(v, current);
        }
      }
    }

    protected abstract @Nullable E retrieveValue();

    protected abstract @Nullable E getIntern();

    protected abstract void setIntern(E value);
  }

  private static class SoftRefConstant<T> extends AbstractCachedConstant<T>{
    private final Constant<? extends T> supplier;
    private volatile SoftReference<T> ref;
    private SoftRefConstant(final Constant<? extends T> supplier) {
      this.supplier = supplier;
    }
    @Override
    protected @Nullable T getIntern() {
      final @Nullable SoftReference<T> ref = this.ref;
      return ref==null?null:ref.get();
    }
    @Override
    protected void setIntern(final T value) {
      assert Thread.holdsLock(lock) && value!=null;
      ref = new SoftReference<>(value);
      assert Thread.holdsLock(lock);
    }
    @Override
    protected T retrieveValue() {
      final T result = supplier.get();
      assert result!=null;
      return result;
    }
  }

  private static class ConstantImp<T>
    extends AbstractCachedConstant<T> implements CachedConstant<T>
  {
    private T value;
    private ThrowingSupplier<? extends T> supplier;
    private ConstantImp(
      final ThrowingSupplier<? extends T> supplier
    ){
      this.supplier = supplier;
    }
    @Override
    protected void setIntern(final T value) {
      assert this.value==null && value!=null && Thread.holdsLock(lock);
      this.value = value;
      supplier = null;
    }
    @Override
    protected T getIntern() {
      return value;
    }
    @Override
    protected T retrieveValue() {
      assert Thread.holdsLock(lock);
      final T value = ConcurrencyUtils.safePublish(supplier);
      supplier = null;
      return value;
    }
  }

  private static class CloseableConstantImp<T>
    extends AbstractCachedConstant<T> implements CloseableCachedConstant<T>
  {
    private T value;
    private ThrowingSupplier<? extends T> supplier;
    private volatile boolean closed = false;
    private final ThrowingConsumer<? super T> closer;
    private CloseableConstantImp(
      final ThrowingSupplier<? extends T> supplier, final ThrowingConsumer<? super T> closer
    ){
      this.supplier = supplier;
      this.closer = closer;
    }
    @Override
    protected void setIntern(final T value) {
      assert this.value==null && value!=null && Thread.holdsLock(lock);
      this.value = value;
    }
    @Override
    protected T getIntern() {
      if(closed) throw new IllegalStateException("Closed.");
      return value;
    }
    @Override
    protected T retrieveValue() {
      assert Thread.holdsLock(lock);
      final T value = ConcurrencyUtils.safePublish(supplier);
      supplier = null;
      return value;
    }
    @Override
    public void close() {
      if(!closed){
        synchronized(lock){
          if(value!=null) call(()->closer.accept(value));
          closed = true;
          value = null;
        }
      }
    }
    @Override
    public boolean closed() {
      return closed;
    }
  }
}
