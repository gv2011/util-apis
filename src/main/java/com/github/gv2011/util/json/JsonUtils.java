package com.github.gv2011.util.json;

import static com.github.gv2011.util.ServiceLoaderUtils.lazyServiceLoader;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import com.github.gv2011.util.Constant;

public class JsonUtils {

  private JsonUtils(){staticClass();}

  private static final Constant<JsonFactory> FACTORY = lazyServiceLoader(JsonFactory.class);

  public static final JsonFactory jsonFactory(){return FACTORY.get();}

  public static String format(final String json) {
    return jsonFactory().deserialize(json).serialize(false);
  }

}
