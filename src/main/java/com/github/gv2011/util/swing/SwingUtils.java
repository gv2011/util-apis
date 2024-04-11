package com.github.gv2011.util.swing;

import static com.github.gv2011.util.ServiceLoaderUtils.lazyServiceLoader;
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.ex.ThrowingSupplier;

public class SwingUtils {

  private SwingUtils(){staticClass();}

  private static final Constant<SwingFactory> FACTORY = lazyServiceLoader(SwingFactory.class);

  public static final SwingFactory swingFactory(){return FACTORY.get();}

  public static final GuiBuilder guiBuilder(){return swingFactory().guiBuilder();}

  public static final <T> T swingCall(final ThrowingSupplier<T> supplier){
    final AtomicReference<T> ref = new AtomicReference<>();
    call(()->{
      SwingUtilities.invokeAndWait(()->{
        ref.set(call(supplier));
      });
    });
    return notNull(ref.get());
  }

}
