package com.github.gv2011.util.http;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.net.URI;

import com.github.gv2011.util.json.JsonNode;

@Deprecated//Use HttpUtils
public class RestUtils {

  private RestUtils(){staticClass();}

  public static final HttpFactory httpFactory(){return HttpUtils.httpFactory();}

  public static JsonNode read(final URI url) {
    try(RestClient client = HttpUtils.httpFactory().createRestClient()){
      return client.read(url);
    }
  }


}
