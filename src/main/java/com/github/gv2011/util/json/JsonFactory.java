package com.github.gv2011.util.json;

import java.io.Reader;
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

  JsonNode deserialize(String json);

  JsonReader jsonReader(Reader in);

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

  default JsonNumber primitive(int i){
    return primitive(NumUtils.from(i));
  }

  default JsonNumber primitive(long i){
    return primitive(NumUtils.from(i));
  }

  default JsonNumber primitive(BigDecimal d){
    return primitive(NumUtils.from(d));
  }

  JsonBoolean primitive(boolean b);

}
