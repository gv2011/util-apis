package com.github.gv2011.util;

import static com.github.gv2011.util.ServiceLoaderUtils.lazyServiceLoader;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.net.URI;

public final class UrlUtils {

    private UrlUtils(){staticClass();}

    private static final Constant<UrlBuilder.Factory> FACTORY = lazyServiceLoader(UrlBuilder.Factory.class);

    public static UrlBuilder newUrlBuilder(){
      return FACTORY.get().newUrlBuilder();
    }

    public static UrlBuilder newUrlBuilder(final URI baseUrl){
      return FACTORY.get().newUrlBuilder(baseUrl);
    }
}
