package com.github.gv2011.util.http;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.icol.Path;


public interface RequestHandler extends AutoCloseableNt{
  
  @Override
  default void close() {}

  boolean accepts(Request request);
  
  default boolean accepts(Path path){
    return true;
  }

  Response handle(Request request);

}
