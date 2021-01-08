package com.github.gv2011.util.bytes;

final class PlainTextImp extends AbstractTypedBytes implements PlainText{
  
  private final Bytes content;

  PlainTextImp(Bytes content) {
    this.content = content;
  }

  @Override
  public final Bytes content() {
    return content;
  }

  @Override
  public final DataType dataType() {
    return DataTypes.TEXT_PLAIN_UTF_8;
  }

}
