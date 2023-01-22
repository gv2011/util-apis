package com.github.gv2011.util.beans;

import com.github.gv2011.util.bytes.HashAndSize;
import com.github.gv2011.util.json.JsonNode;

public interface Type<T> {

    String name();

    T parse(JsonNode json);

    T parse(String string);

    boolean isInstance(Object object);

    T cast(Object object);

    JsonNode toJson(T object);

    default HashAndSize hashAndSize(final T object){
      return toJson(object).hash();
    }

}
