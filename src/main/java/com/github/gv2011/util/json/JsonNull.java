package com.github.gv2011.util.json;

import com.github.gv2011.util.icol.Nothing;

public interface JsonNull extends JsonPrimitive<Nothing>{

  @Override
  public JsonNull filter(final String attribute);

}
