package com.github.gv2011.util.ex;

import static com.github.gv2011.util.icol.ICollections.asList;
import static com.github.gv2011.util.icol.ICollections.iCollections;
import static com.github.gv2011.util.icol.ICollections.nothing;
import static com.github.gv2011.util.icol.ICollections.toIList;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import com.github.gv2011.util.OptCloseable;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Nothing;
import com.github.gv2011.util.icol.Opt;

public final class Exceptions {

  private Exceptions(){staticClass();}

  public static final boolean ASSERTIONS_ON;

  private static @Nullable Logger logger = null;

  static{
    boolean on = false;
    assert on=true;
    ASSERTIONS_ON = on;
  }

  public static RuntimeException notYetImplementedException(){
    return new NotYetImplementedException();
  }

  public static RuntimeException notYetImplementedException(final String msg){
    return new NotYetImplementedException(msg);
  }

  public static <T> T notYetImplemented(){
    throw notYetImplementedException();
  }

  public static <T> T notYetImplemented(final String msg){
    throw notYetImplementedException(msg);
  }

  public static <T> T illegalArgument(final @Nullable Object arg){
    throw new IllegalArgumentException(String.valueOf(arg));
  }

  public static <T> T unsupported(){
    throw new UnsupportedOperationException();
  }

  public static RuntimeException bug(){
    return new Bug();
  }

  public static <T> T bugValue(){
    throw bug();
  }

  public static RuntimeException bug(final Supplier<String> message){
    return new Bug(message.get());
  }

  public static RuntimeException wrap(final Throwable e){
    if(e instanceof RuntimeException) return (RuntimeException)e;
    else return map(e, e.getMessage());
  }

  public static RuntimeException wrap(final Exception e, final String msg){
    return map(e, msg);
  }

  private static RuntimeException map(final Throwable e, final String msg){
    if(e instanceof InterruptedException)
      return new InterruptedRtException((InterruptedException) e, msg);
    else if(e instanceof InterruptedIOException)
      return new InterruptedRtException((InterruptedIOException) e, msg);
    else if(e instanceof IOException)
      return new UncheckedIOException(msg, (IOException) e);
    else return new WrappedException(e, msg);
  }

  public static Runnable logExceptions(final Runnable r){
    return ()->{
      try {
        r.run();
      }
      catch (final Throwable t) {
        getLogger().error(format("Exception in {}.", Thread.currentThread()), t);
      }
    };
  }

  public static String format(final String pattern, final @Nullable Object... arguments) {
    return MessageFormatter.arrayFormat(pattern, arguments).getMessage();
  }

  public static <R> R call(final ThrowingSupplier<R> throwing, final Supplier<?> message){
    return throwing.asFunction(message).apply(null);
  }

  public static <R> R call(final ThrowingSupplier<R> throwing){
    return throwing.asFunction().apply(null);
  }

  public static <R> Opt<R> tryCall(final ThrowingSupplier<R> throwing){
    return tryCall(throwing.asFunction(), e->{});
  }

  public static <R> Opt<R> tryCall(final ThrowingSupplier<R> throwing, final Consumer<Throwable> errorHandler){
    return tryCall(throwing.asFunction(), errorHandler);
  }

  public static <I,R> R tryCall(
    final ThrowingSupplier<I> throwing,
    final Function<I,R> mapping,
    final Function<Throwable,R> errorHandler
  ){
    final AtomicReference<Throwable> ref = new AtomicReference<>();
    return tryCall(throwing, ex->ref.set(ex))
      .map(i->mapping.apply(i))
      .orElseGet(()->errorHandler.apply(ref.get()))
    ;
  }

  public static Opt<Nothing> tryCall(final ThrowingRunnable throwing, final Consumer<Throwable> errorHandler){
    return tryCall(throwing.asFunction(), errorHandler);
  }

//  public static <R> Opt<R> tryCall(final Supplier<R> throwing){
//    return tryCall(a->throwing.get());
//  }

  public static boolean tryCall(final ThrowingRunnable throwing){
    return tryCall(throwing.asFunction(), e->{}).isPresent();
  }

  private static <R> Opt<R> tryCall(final Function<Object, R> f, final Consumer<Throwable> errorHandler){
    final R result;
    try {
      result = f.apply(null);
    } catch (final Exception e) {
      errorHandler.accept(e);
      return iCollections().empty();
    }
    return iCollections().single(result);
  }

  public static Nothing call(final ThrowingRunnable throwing){
    return throwing.asFunction().apply(null);
  }

  public static final ThrowingSupplier<Nothing> supplier(final ThrowingRunnable runnable) {
    return ()->{
      runnable.runThrowing();
      return nothing();
    };
  }

  public static Nothing tryAll(final ThrowingRunnable... operations){
    return tryAll(Arrays.asList(operations));
  }

  /**
   * Tries to do all the operations even if some throw exceptions.
   * All but the last exception will be logged, but otherwise ignored.
   * @return
   */
  public static Nothing tryAll(
    final List<ThrowingRunnable> operations
  ){
    if(operations.isEmpty()){} //do nothing
    else if(operations.size()==1) {
      call(()->operations.get(0).runThrowing());
    }
    else{
      Throwable t1 = null;
      try{
        try{operations.get(0).runThrowing();}
        catch(final Throwable t){
          t1 = t;
          throw wrap(t);
        }
      }finally{
        try{tryAll(operations.subList(1, operations.size()));}
        catch(final Throwable t2){
          if(t1!=null){
            //Log this exception, because it is hidden by t2.
            getLogger().error(t1.getMessage(), t1);
          }
          throw t2;
        }
      }
    }
    return nothing();
  }

  /**
   * Logs the exception if assertions are off, otherwise throws it.
   */
  public static final void tolerate(final Throwable t){
    if(ASSERTIONS_ON){
      throw wrap(t);
    }
    else getLogger().error(t.getMessage()+" (tolerated)", t);
  }

  public static void staticClass(){
    throw new RuntimeException("This is a static class without instances.");
  }

  public static <C extends OptCloseable,R> R callWithOptCloseable(
      final ThrowingSupplier<C> supplier, final ThrowingFunction<C,R> function
    ){
      return callWithCloseable(supplier, function, OptCloseable::close);
    }

  public static <C extends AutoCloseable,R> R callWithCloseable(
    final ThrowingSupplier<C> supplier, final ThrowingFunction<C,R> function
  ){
    return callWithCloseable(supplier, function, AutoCloseable::close);
  }

  public static <C,R> R callWithCloseable(
    final ThrowingSupplier<C> supplier,
    final ThrowingFunction<C,R> function,
    final ThrowingConsumer<? super C> closer
  ){
    try{
      final C closeable = supplier.getThrowing();
      try{
        return function.applyThrowing(closeable);
      }finally{
        closer.accept(closeable);
      }
    }
    catch(final Exception ex){throw wrap(ex);}
  }

  public static <C extends AutoCloseable, I extends AutoCloseable, R> R callWithCloseable(
    final ThrowingSupplier<C> supplier,
    final ThrowingFunction<C,I> wrapper,
    final ThrowingFunction<I,R> function
  ){
    @Nullable I wrapped = null;
    try{
      final C closeable = supplier.getThrowing();
      try{
        wrapped = wrapper.applyThrowing(closeable);
        return function.applyThrowing(wrapped);
      }finally{
        if(wrapped == null) closeable.close();
        else wrapped.close();
      }
    }
    catch(final Exception ex){throw wrap(ex);}
  }

  public static <C extends AutoCloseable> Nothing callWithCloseable(
    final ThrowingSupplier<C> supplier, final ThrowingConsumer<C> consumer
  ){
    callWithCloseable(supplier, consumer, AutoCloseable::close);
    return nothing();
  }

  public static <C extends OptCloseable> Nothing callWithOptCloseable(
    final ThrowingSupplier<C> supplier, final ThrowingConsumer<C> consumer
  ){
    return callWithCloseable(supplier, consumer, OptCloseable::close);
  }

  public static <C> Nothing callWithCloseable(
    final ThrowingSupplier<C> supplier,
    final ThrowingConsumer<C> consumer,
    final ThrowingConsumer<? super C> closer
  ){
    try{
      final C closeable = supplier.getThrowing();
      try{consumer.accept(closeable);}
      finally{closer.accept(closeable);}
    }
    catch(final Exception ex){throw wrap(ex);}
    return nothing();
  }

  public static <CI extends AutoCloseable, CO extends AutoCloseable> CO wrapCloseable(
    final CI inner, final ThrowingFunction<CI, CO> wrapping
  ) {
    boolean success = false;
    try {
      final CO result = wrapping.applyThrowing(inner);
      success = false;
      return result;
    } catch (final Exception e) {
      throw wrap(e);
    } finally {
      if(!success) call(inner::close);
    }
  }

	  /**
   * Lazy creation because of bootstrapping.
   */
  private static Logger getLogger() {
    Logger logger = Exceptions.logger;
    if(logger==null){
      logger = LoggerFactory.getLogger(Exceptions.class);
      Exceptions.logger = logger;
    }
    return logger;
  }

  public static Nothing closeAll(final AutoCloseable... closeables){
    return closeAll(asList(closeables));
  }

  public static Nothing closeAll(final IList<? extends AutoCloseable> closeables){
    return tryAll(closeables.stream().map(c->(ThrowingRunnable)(c::close)).collect(toIList()));
  }
}
