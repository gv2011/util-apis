package com.github.gv2011.util.json;

import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;

import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collector;

import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.num.Decimal;
import com.github.gv2011.util.num.NumUtils;
import com.github.gv2011.util.serviceloader.Service;

@Service(defaultImplementation="com.github.gv2011.util.json.imp/com.github.gv2011.util.json.imp.JsonFactoryImp")
public interface JsonFactory {

  static final boolean COMPACT_DEFAULT = false;

  JsonNode deserialize(String json);

  default String serialize(final JsonNode json){
    return serialize(json, COMPACT_DEFAULT);
  }

  default String serialize(final JsonNode json, final boolean compact){
    return callWithCloseable(StringWriter::new, sw->{
      final JsonWriter jsonWriter = jsonWriter(sw, compact);
      json.write(jsonWriter);
      jsonWriter.flush();
      return sw.toString();
    });
  }

  JsonReader jsonReader(Reader in);

  JsonWriter jsonWriter(Writer writer, final boolean compact);

  JsonList asJsonList(IList<?> list, Function<Object,JsonNode> converter);

  Collector<JsonNode,?,JsonList> toJsonList();

  <T> Collector<T, ?, JsonObject> toJsonObject(
    final Function<? super T, String> keyMapper,
    final Function<? super T, JsonNode> valueMapper
  );

  JsonNode emptyList();


  Collector<Entry<String,JsonNode>, ?, JsonObject> toJsonObject();

  JsonNull jsonNull();

  JsonString primitive(String s);

  JsonString primitive(Bytes b);

  JsonNumber primitive(Decimal number);

  default JsonNumber primitive(final int i){
    return primitive(NumUtils.from(i));
  }

  default JsonNumber primitive(final long i){
    return primitive(NumUtils.from(i));
  }

  default JsonNumber primitive(final BigDecimal d){
    return primitive(NumUtils.from(d));
  }

  JsonBoolean primitive(boolean b);

}
