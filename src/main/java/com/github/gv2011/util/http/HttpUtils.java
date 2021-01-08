package com.github.gv2011.util.http;

import static com.github.gv2011.util.ServiceLoaderUtils.lazyServiceLoader;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.net.URI;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.json.JsonNode;

public final class HttpUtils {

  private HttpUtils(){staticClass();}

  private static final Constant<HttpFactory> FACTORY = lazyServiceLoader(HttpFactory.class);

  public static final HttpFactory httpFactory(){return FACTORY.get();}

  public static JsonNode read(final URI url) {
    try(RestClient client = httpFactory().createRestClient()){
      return client.read(url);
    }
  }


}
