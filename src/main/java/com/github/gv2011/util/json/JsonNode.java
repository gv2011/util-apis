package com.github.gv2011.util.json;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.json.JsonUtils.jsonFactory;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.OutputStreamWriter;
import java.util.stream.Stream;

import com.github.gv2011.util.HashUtils;
import com.github.gv2011.util.bytes.HashAndSize;
import com.github.gv2011.util.bytes.HashFactory.HashBuilder;
import com.github.gv2011.util.num.Decimal;

public interface JsonNode extends Comparable<JsonNode>{

  JsonNodeType jsonNodeType();

  default String serialize() {
    return serialize(JsonFactory.COMPACT_DEFAULT);
  }

  String serialize(boolean compact);

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

  int compareWithOtherOfSameJsonNodeType(JsonNode o);

  String rawToString();

  default HashAndSize hash() {
    final HashBuilder hashBuilder = HashUtils.hashBuilder();
    final OutputStreamWriter writer = new OutputStreamWriter(hashBuilder.outputStream(), UTF_8);
    write(jsonFactory().jsonWriter(writer,true));
    call(writer::flush);
    return hashBuilder.build();
  }
}
