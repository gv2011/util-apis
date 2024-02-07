package com.github.gv2011.util.http;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.icol.Path;

@FunctionalInterface
public interface RequestHandler extends AutoCloseableNt{

  @Override
  default void close() {}

  default boolean accepts(final Request request){
    return true;
  }

  default boolean accepts(final Path path){
    return true;
  }

  Response handle(Request request);

}
