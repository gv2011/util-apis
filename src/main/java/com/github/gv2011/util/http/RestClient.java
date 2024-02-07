package com.github.gv2011.util.http;

import java.net.URI;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.json.JsonNode;

public interface RestClient extends AutoCloseableNt{

  public JsonNode read(final URI url);

  public JsonNode post(final URI url, JsonNode body);

}
