package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.io.Reader;

import com.github.gv2011.util.ex.ThrowingSupplier;

public final class ReaderUtils {

  private ReaderUtils(){staticClass();}

  public static String readText(final ThrowingSupplier<Reader> in){
    return callWithCloseable(in, r->{
      final StringBuilder out = new StringBuilder();
      final char[] buffer = new char[1024];
      int read = r.read(buffer);
      while(read!=-1){
        out.append(buffer, 0, read);
        read = r.read(buffer);
      }
      return out.toString();
    });
  }

}
