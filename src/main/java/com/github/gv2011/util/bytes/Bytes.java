package com.github.gv2011.util.bytes;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import com.github.gv2011.util.HashAlgorithm;
import com.github.gv2011.util.OptCloseable;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.ann.Immutable;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.uc.UStr;
import static com.github.gv2011.util.bytes.ByteUtils.UTF8BOM;

@Immutable
public interface Bytes extends List<Byte>, Comparable<Bytes>, OptCloseable{

  public static final class TooBigException extends IllegalStateException {}

  Bytes append(Bytes other);

  Hash256 asHash();

  Bytes decodeBase64();

  byte get(long index);

  int getUnsigned(long index);

  default Hash256 hash(){
    return hashAndSize().hash();
  }

  HashAndSize hashAndSize();

  Hash hash(HashAlgorithm hashAlgorithm);

  Opt<Long> indexOfOther(Bytes other);

  Opt<Long> findStartOfOther(Bytes other);

  long longSize();

  InputStream openStream();

  Reader reader();

  @Override
  int size() throws TooBigException;

  Pair<Bytes,Bytes> split(long index);

  boolean startsWith(Bytes prefix);

  @Override
  Bytes subList(int fromIndex, int toIndex);

  Bytes subList(final long fromIndex, final long toIndex);

  default Bytes subList(final long fromIndex){return subList(fromIndex, longSize());}

  BigInteger toBigInteger();

  Bytes toBase64();

  String toBase64String();

  byte[] toByteArray() throws TooBigException;

  String toHexMultiline();

  String toHex();

  long toLong();

  String toHexColon();

  int toInt();

  String toString(Charset charset);

  String utf8ToString() throws TooBigException;

  UStr utf8ToUStr() throws TooBigException;

  int write(byte[] b, int off, int len);

  void write(final OutputStream stream);

  default void write(final Path file){
    write(file, false);
  }

  void write(final Path file, boolean onlyOwner);

  byte getByte(int i);

  default byte getByte(final long i){
    return get(i);
  }

  Bytes subList(int fromIndex);

  TypedBytes typed();

  TypedBytes typed(DataType mimeType);

  ByteBuffer toBuffer(long offset);

  ByteBuffer toBuffer(long offset, int size);

  int write(final ByteBuffer buffer, final long offset);

  @Override
  ByteIterator.Resettable iterator();

  default Bytes replaceAll(final Bytes sequence, final Bytes replacement){
    return replaceAll(replacement, replacement, pos->{});
  }

  Bytes replaceAll(Bytes sequence, Bytes replacement, Consumer<Long> positions);

  default Bytes removeBom(){
    return startsWith(UTF8BOM) ? subList(UTF8BOM.size()) : this;
  }

}
