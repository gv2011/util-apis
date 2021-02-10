package com.github.gv2011.util.bytes;

import java.security.MessageDigest;

final class Hash256Imp extends AbstractTypedBytes implements Hash256{

  private final Bytes content;

  Hash256Imp(final MessageDigest md) {
    this(md.digest());
  }

  Hash256Imp(final byte[] byteArray) {
    this(ArrayBytes.create(byteArray));
  }

  Hash256Imp(final Bytes bytes) {
    if(bytes.longSize()!=(long)Hash256.SIZE) throw new IllegalArgumentException("Length is "+bytes.longSize());
    content = bytes;
  }

  protected String toStringImp() {
    return content.toHexColon();
  }

  @Override
  public Bytes content() {
    return content;
  }
}
