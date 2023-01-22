package com.github.gv2011.util.tempfile;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.ServiceLoaderUtils;


/**
 * Convenience class with static methods that mirror the methods of the default {@link TempFileFactory}.
 */
public final class TempFiles {

  private TempFiles(){staticClass();}

  private static final Constant<TempFileFactory> TFF = Constants.softRefConstant(
      ()->ServiceLoaderUtils.loadService(TempFileFactory.class)
  );

  public static final TempFileFactory tempFileFactory(){return TFF.get();}

  public static final TempDir createTempDir() {return tempFileFactory().createTempDir();}
}
