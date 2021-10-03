package com.github.gv2011.util;

import static com.github.gv2011.util.Verify.notNull;

import java.io.IOException;
import java.io.Reader;
import java.util.stream.IntStream;

public final class CharSequenceReader extends Reader{

  private final Object lock = new Object();

  private CharSequence chars;
  private int position;

  public CharSequenceReader(final CharSequence immutableCharSequence) {
    chars = immutableCharSequence;
    position = 0;
  }

  @Override
  public int read(final char[] cbuf, final int off, final int len) throws IOException {
    if(off<0 || len<0 || len>cbuf.length - off) throw new IndexOutOfBoundsException();
    if(len==0) return 0;
    else{
      synchronized(lock){
        notNull(chars);
        final int remaining = chars.length()-position;
        if(remaining==0) return -1;
        else{
          final int count = Math.min(remaining, len);
          assert count>0;
          IntStream.range(0, count).parallel().forEach(i->cbuf[off+i]=chars.charAt(position+i));
          position+=count;
          return count;
        }
      }
    }
  }

  @Override
  public void close() throws IOException {
    synchronized(lock){
      chars = null;
    }
  }

}
