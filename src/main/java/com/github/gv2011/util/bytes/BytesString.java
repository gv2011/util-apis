package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.ex.Exceptions.notYetImplemented;
import static com.github.gv2011.util.num.NumUtils.toInt;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.List;

final class BytesString implements CharSequence{

  private static final int BLOCK_SIZE = 65536;

  private final Bytes bytes;

  BytesString(final Bytes bytes) {
    this.bytes = bytes;
  }

  @Override
  public int length() {
    return toInt(longLength());
  }

  public long longLength() {
    final List<Block> blocks = blocks();
    assert !blocks.isEmpty();
    return (blocks.size()-1L) * ((long)BLOCK_SIZE) + blocks.get(blocks.size()-1).size;
  }

  @Override
  public char charAt(final int index) {
    return charAt((long)index);
  }

  public char charAt(final long index) {
    final int b = toInt(index / BLOCK_SIZE);
    final int pos = toInt(index % BLOCK_SIZE);
    return blocks().get(b).charAt(pos);
  }

  @Override
  public CharSequence subSequence(final int start, final int end) {
    return notYetImplemented();
  }

  private List<Block> blocks(){
      final long position = 0L;
      final List<Block> result = new ArrayList<>();
      final CharsetDecoder decoder = UTF_8.newDecoder()
        .onMalformedInput(CodingErrorAction.REPORT)
        .onUnmappableCharacter(CodingErrorAction.REPORT)
      ;
      final ByteBuffer inBuffer = bytes.toBuffer(position);
      final boolean atEnd = !inBuffer.hasRemaining();

      final CharBuffer outBuffer = CharBuffer.allocate(BLOCK_SIZE);
      while(!atEnd){
        while(inBuffer.hasRemaining()){
          final CoderResult r = decoder.decode(inBuffer, outBuffer, false);
          if(r.isUnderflow()){

          }
          else if(r.isOverflow()){
            outBuffer.toString();
          }
        }
      }
      return result;
  }

  private static final class Block{
    private int size;
    private SoftReference<byte[]> chars;

    private Block(final char[] buffer, final int total) {
      notYetImplemented();
    }

    private char charAt(final int pos) {
      // TODO Auto-generated method stub
      return notYetImplemented();
    }
  }

}
