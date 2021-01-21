package com.github.gv2011.util.http;

import java.util.OptionalInt;

import com.github.gv2011.util.AutoCloseableNt;

public interface HttpServer extends AutoCloseableNt{

  OptionalInt httpsPort();
  
  int httpPort();
  
}
