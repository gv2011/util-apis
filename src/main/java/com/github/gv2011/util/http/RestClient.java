package com.github.gv2011.util.http;

import java.net.URI;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonNode;

public interface RestClient extends AutoCloseableNt{

  public JsonNode read(final URI url);

  public default JsonNode post(final URI url, final JsonNode body){
    return post(url, body, Opt.empty());
  }

  public JsonNode post(final URI url, JsonNode body, Opt<String> authToken);

}
