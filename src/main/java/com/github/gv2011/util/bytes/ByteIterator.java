package com.github.gv2011.util.bytes;

import java.io.InputStream;
import java.util.Iterator;
import java.util.PrimitiveIterator;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.StreamUtils;

public interface ByteIterator extends Iterator<Byte>, AutoCloseableNt{

  default byte nextByte(){
    return next();
  }

  default String collectToString(){
    return StreamUtils.readText(this::asStream);
  }

  default PrimitiveIterator.OfInt asIntIterator(){
    return new PrimitiveIterator.OfInt(){
      @Override
      public boolean hasNext() {return ByteIterator.this.hasNext();}
      @Override
      public int nextInt() {
        return Byte.toUnsignedInt(nextByte());
      }
    };
  }

  default InputStream asStream(){
    return new InputStream(){
      @Override
      public int read(){return hasNext() ? Byte.toUnsignedInt(nextByte()) : -1;}
      @Override
      public void close(){ByteIterator.this.close();}
    };
  }

  static interface Resettable extends ByteIterator{
    void reset();
  }

}
