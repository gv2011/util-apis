package com.github.gv2011.util.json;

import java.util.stream.Stream;

import com.github.gv2011.util.num.Decimal;

public interface JsonNode extends Comparable<JsonNode>{

  JsonNodeType jsonNodeType();

  String serialize();

  void write(final JsonWriter out);

  JsonNode filter(String attribute);

  Stream<JsonNode> stream();

  JsonObject asObject();

  JsonList asList();

  String asString();

  Decimal asNumber();

  boolean asBoolean();

  JsonNull asNull();

  default boolean isNull() {return false;}

}
