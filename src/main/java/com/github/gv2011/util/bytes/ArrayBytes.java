package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.ex.Exceptions.call;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Arrays;

import com.github.gv2011.util.ann.Immutable;

@Immutable
class ArrayBytes extends AbstractBytes{
  
  static final AbstractBytes EMPTY = new ArrayBytes(new byte[0]);

  private final byte[] bytes;
  
  static AbstractBytes create(final byte[] bytes){
    return bytes.length==0 ? EMPTY : new ArrayBytes(bytes);
  }

  private ArrayBytes(final byte[] bytes) {
    this.bytes = bytes;
  }

  @Override
  protected Hash256 hashImp() {
    final MessageDigest md = call(()->MessageDigest.getInstance("SHA-256"));
    md.update(bytes);
    return new Hash256Imp(md);
  }


  @Override
  public byte[] toByteArray(){
    return Arrays.copyOf(bytes, bytes.length);
  }

  @Override
  public void write(final OutputStream stream){
    call(()->stream.write(bytes));
  }

  @Override
  public long longSize() {
    return bytes.length;
  }

  @Override
  public byte get(final long index) {
    if(index>Integer.MAX_VALUE) throw new IndexOutOfBoundsException();
    return bytes[(int)index];
  }

  @Override
  public byte getByte(final int index) {
    return bytes[index];
  }

  @Override
  public Bytes subList(final long fromIndex, final long toIndex) {
    final long size = longSize();
    checkIndices(fromIndex, toIndex, size);
    if(fromIndex==0 && toIndex==size) return this;
    else{
      return new ArrayBytes(Arrays.copyOfRange(bytes, (int)fromIndex, (int)toIndex));
    }
  }

  @Override
  public String utf8ToString() throws TooBigException {
    return new String(bytes, UTF_8);
  }

  @Override
  public InputStream openStream() {
    return new ByteArrayInputStream(bytes);
  }

  @Override
  public Hash256 asHash() {
    return new Hash256Imp(bytes);
  }

}
