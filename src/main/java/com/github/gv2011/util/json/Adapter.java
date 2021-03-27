package com.github.gv2011.util.json;

import java.io.Reader;
import java.io.Writer;

import com.github.gv2011.util.serviceloader.Service;

@Service(defaultImplementation="com.github.gv2011.gsoncore/com.github.gv2011.gsoncore.GsoncoreAdapter")
public interface Adapter {

  JsonNode deserialize(JsonFactory jsonFactory, String json);

  JsonReader newJsonReader(final JsonFactory jf, Reader in);

  JsonWriter newJsonWriter(Writer out);
  
  default boolean isCanonical(){
    return false;
  }

}
