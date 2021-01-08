package com.github.gv2011.util.bytes;

public interface PlainText extends TypedBytes{
  
  static PlainText plainText(String s){
    return ByteUtils.asUtf8(s);
  }
  
  @Override
  default DataType dataType() {
    return DataTypes.TEXT_PLAIN_UTF_8;
  }
  
  default String asString(){
    return content().utf8ToString();
  }

}
