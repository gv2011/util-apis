package com.github.gv2011.util.beans;

import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;

public interface ElementaryTypeHandler<T>{

  T fromJson(JsonNode json);

  JsonNode toJson(T object, JsonFactory jf);

  default Opt<T> defaultValue(){
    return Opt.empty();
  }

  default JsonNodeType jsonNodeType() {
    return JsonNodeType.STRING;
  }

  default boolean isInstance(final Class<T> clazz, final Object object) {
      return clazz.isInstance(object);
  }

  default T cast(final Class<T> clazz, final Object object) {
      return clazz.cast(object);
  }

  default boolean hasStringForm(){
    return false;
  }

  T parse(String string);

}
