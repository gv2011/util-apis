package com.github.gv2011.util.http;

import com.github.gv2011.util.tstr.TypedString;

public interface Method extends TypedString<Method>{
  
  static final Method GET = TypedString.create(Method.class, "GET");

}
