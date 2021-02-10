package com.github.gv2011.util.bytes;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.github.gv2011.util.OptCloseable;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.ann.Immutable;
import com.github.gv2011.util.uc.UStr;


@Immutable
public interface Bytes extends List<Byte>, Comparable<Bytes>, OptCloseable{

  public static final class TooBigException extends IllegalStateException {}

  Bytes append(Bytes hashBytes);

  Hash256 asHash();

  Bytes decodeBase64();

  byte get(long index);

  int getUnsigned(long index);

  Hash256 hash();

  Optional<Long> indexOfOther(Bytes other);

  long longSize();

  InputStream openStream();

  @Override
  int size() throws TooBigException;

  Pair<Bytes,Bytes> split(long index);

  boolean startsWith(Bytes prefix);

  @Override
  Bytes subList(int fromIndex, int toIndex);

  Bytes subList(final long fromIndex, final long toIndex);

  Bytes toBase64();

  String toBase64String();

  byte[] toByteArray() throws TooBigException;

  String toHexMultiline();

  String toHex();

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

  Bytes subList(int fromIndex);

  TypedBytes typed();

  TypedBytes typed(DataType mimeType);



}
