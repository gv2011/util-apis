package com.github.gv2011.util.bytes;

import java.util.function.Consumer;

public interface ByteConsumer extends Consumer<Byte>{

  default void acceptByte(final byte b){
    accept((Byte) b);
  }

  default boolean spaceAvailable(){
    return true;
  }
}
