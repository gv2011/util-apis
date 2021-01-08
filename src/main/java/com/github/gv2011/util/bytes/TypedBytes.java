package com.github.gv2011.util.bytes;

public interface TypedBytes {

  Bytes content();

  default DataType dataType() {
    return DataTypes.APPLICATION_OCTET_STREAM;
  }

}
