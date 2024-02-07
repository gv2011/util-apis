package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.bugValue;
import static java.nio.charset.CodingErrorAction.REPORT;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

final class ReaderInputStream extends InputStream{

  private final Object lock = new Object();
  private final CharsetEncoder encoder;
  private final Reader reader;
  private final CharBuffer charBuffer;
  private final byte[] singleByte = new byte[1];
  private boolean endOfInput = false;

  ReaderInputStream(final Reader reader, final Charset charset){
    this.reader = reader;
    charBuffer = CharBuffer.allocate(4096);
    charBuffer.mark();
    encoder = charset.newEncoder().onMalformedInput(REPORT).onUnmappableCharacter(REPORT);
  }

  @Override
  public int read() throws IOException {
    synchronized(lock){
      final int count = read(singleByte);
      return count==1
        ? Byte.toUnsignedInt(singleByte[0])
        : count==-1 ? -1 : bugValue()
      ;
    }
  }

  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    synchronized(lock){
      if(!endOfInput && charBuffer.hasRemaining()) {
        endOfInput = reader.read(charBuffer)==-1;
        if(endOfInput) reader.close();
      }
      charBuffer.limit(charBuffer.position());
      charBuffer.reset();
      if(endOfInput && !charBuffer.hasRemaining()){
        return -1;
      }
      else{
        final ByteBuffer bb = ByteBuffer.wrap(b, off, len);
        final CoderResult cr = encoder.encode(charBuffer, bb, endOfInput);
        if(cr.isError()) cr.throwException();
        if(charBuffer.hasRemaining()){
          charBuffer.mark();
          charBuffer.position(charBuffer.limit());
          charBuffer.limit(charBuffer.capacity());
        }
        else{
          charBuffer.clear();
          charBuffer.mark();
        }
        return bb.position()-off;
      }
    }
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

}
