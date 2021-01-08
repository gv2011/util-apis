package com.github.gv2011.util.html;

import static com.github.gv2011.util.ServiceLoaderUtils.lazyServiceLoader;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import com.github.gv2011.util.Constant;

public final class HtmlUtils {
  
  private HtmlUtils(){staticClass();}

  private static final Constant<HtmlFactory> FACTORY = lazyServiceLoader(HtmlFactory.class);

  public static final HtmlFactory htmlFactory(){return FACTORY.get();}

}
