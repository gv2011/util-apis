package com.github.gv2011.util.bytes;

import com.github.gv2011.util.Equal;

public abstract class AbstractTypedBytes implements TypedBytes{

  @Override
  public int hashCode() {
    return dataType().hashCode() * 31 + content().hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    return Equal.equal(
      this, obj, TypedBytes.class, o->o.dataType().equals(dataType()) && o.content().equals(content())
    );
  }

  @Override
  public String toString() {
    return dataType()+":"+content();
  }



}
