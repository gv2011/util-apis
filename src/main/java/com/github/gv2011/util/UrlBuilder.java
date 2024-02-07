package com.github.gv2011.util;

import java.net.URI;

import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IMap;

public interface UrlBuilder {

  static interface Factory{
    UrlBuilder newUrlBuilder();
    UrlBuilder newUrlBuilder(URI baseUrl);
  }

  UrlBuilder setQuery(final IMap<String, String> query);

  UrlBuilder setQuery(IList<Pair<String,String>> query);

  URI build();

}
