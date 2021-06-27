package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.num.NumUtils.isOdd;
import static java.lang.Math.min;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.IntStream;

import com.github.gv2011.util.FileUtils;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.ex.ThrowingSupplier;
import com.github.gv2011.util.icol.Opt;

public class ByteUtils {

  private ByteUtils(){staticClass();}

  private static final String HEX_CHARS = "0123456789ABCDEF";

  private static final Map<DataType,Function<Bytes,TypedBytes>> TYPED_CONSTRUCTORS = createTypedConstructors();

  public static Bytes newBytes(final byte... bytes){
    return newBytes(bytes, 0, bytes.length);
  }

  public static Bytes newBytes(final int... bytes){
    final byte[] a = new byte[bytes.length];
    for(int i=0; i<a.length; i++) a[i] = (byte) bytes[i];
    return ArrayBytes.create(a);
  }

  public static Bytes newBytes(final byte[] bytes, final int from, final int to) {
    final byte[] copy = Arrays.copyOfRange(bytes, from, to);
    return ArrayBytes.create(copy);
  }



  public static Bytes parseHex(final String hex){
    return ArrayBytes.create(hexToByteArray(hex));
  }

  public static byte[] hexToByteArray(final CharSequence hex){
    final String noWhitespace = removeWhitespaceAndColon(hex);
    if(isOdd(noWhitespace.length())) throw new IllegalArgumentException();
    final int size = noWhitespace.length()/2;
    final byte[] b = new byte[size];
    for(int i=0; i<size; i++){
      b[i] = (byte)(
        Character.digit(noWhitespace.charAt(i*2)  , 16)<<4 |
        Character.digit(noWhitespace.charAt(i*2+1), 16)
      );
    }
    return b;
  }

  static String removeWhitespaceAndColon(final CharSequence s) {
    return s.toString().replaceAll("[\\s:]+", "");
  }


  public static Bytes newBytes(final Bytes bytes){
    return ArrayBytes.create(bytes.toByteArray());
  }

  public static Bytes asBytes(final int i){
    final byte[] array = new byte[4];
    array[3] = (byte)(i);
    array[2] = (byte)(i>>8);
    array[1] = (byte)(i>>16);
    array[0] = (byte)(i>>24);
    return ArrayBytes.create(array);
  }

  public static PlainText asUtf8(final String text){
    return new PlainTextImp(ArrayBytes.create(text.getBytes(UTF_8)));
  }

  public static Hash256 hash(final String text){
    return call(()->
      new Hash256Imp(Hash256Imp.ALGORITHM.createMessageDigest().digest(text.getBytes(UTF_8)))
    );
  }

  public static Pair<InputStream,Supplier<Hash256>> hashStream(final InputStream in){
    final MessageDigest md = call(()->Hash256Imp.ALGORITHM.createMessageDigest());
    final InputStream din = new DigestInputStream(in, md);
    return pair(din, ()->new Hash256Imp(md));
  }

  public static Bytes newRandomBytes(final long size){
    final byte[] bytes = new byte[(int)min(1024,size)];
    final Random random = new SecureRandom();
    long remaining = size;
    try(BytesBuilder builder = newBytesBuilder()){
      while(remaining>0){
        random.nextBytes(bytes);
        final int len = (int)min(bytes.length, remaining);
        builder.write(bytes,0,len);
        remaining-=len;
      }
      return builder.build();
    }
  }

  public static Bytes fromStream(final InputStream in){
    final byte[] bytes = new byte[1024];
    int count = call(()->in.read(bytes));
    try(BytesBuilder builder = newBytesBuilder()){
      while(count!=-1){
        builder.write(bytes,0,count);
        count = call(()->in.read(bytes));
      }
      return builder.build();
    }
  }

  public static Bytes copyFromStream(final ThrowingSupplier<InputStream> in){
    return callWithCloseable(
      in::get,
      s->{
        final byte[] buffer = new byte[1024];
        int count = s.read(buffer);
        final BytesBuilder builder = newBytesBuilder();
        while(count!=-1){
          builder.write(buffer,0,count);
          count = s.read(buffer);
        }
        return builder.build();
      }
    );
  }

  public static Bytes copyFromStream(final InputStream in, final long limit) {
    verify(limit>=0);
    if(limit==0) return ArrayBytes.EMPTY;
    else{
      final BytesBuilder builder = newBytesBuilder();
      long remaining = limit;
      final byte[] buffer = new byte[(int)Math.min(1024, remaining)];
      int count = call(()->in.read(buffer));
      while(remaining>0){
        if(count==-1) remaining=-1;
        else{
          builder.write(buffer,0,count);
          remaining-=count;
          final int readLimit = (int) Math.min(buffer.length, remaining);
          count = call(()->in.read(buffer,0,readLimit));
        }
      }
      return builder.build();
    }
  }

  public static Bytes collectBytes(final IntStream intStream) {
    return intStream
      .collect(BytesBuilder::new, BytesBuilder::write, (b1,b2)->b1.append(b2.build()))
      .build()
    ;
  }


  public static Opt<Bytes> tryRead(final Path file) {
    return Files.exists(file) ? Opt.of(read(file)) : Opt.empty();
  }

  public static Bytes read(final Path file) {
    return new FileBytes(file);
  }

  public static Bytes read(final URL url) {
    return copyFromStream(url::openStream);
  }

  public static TypedBytes readTyped(final Path file) {
    DataType dataType = DataTypeProvider.instance().dataTypeForExtension(FileUtils.getExtension(file));
    if(dataType.charset().isEmpty() && dataType.primaryType().equals(DataTypes.TEXT)){
      dataType = dataType.withCharset(UTF_8);
    }
    final FileBytes bytes = new FileBytes(file);
    return createTyped(bytes, dataType);
  }

  public static TypedBytes readTyped(final URL url) {
    DataType dataType = DataTypeProvider.instance().dataTypeForExtension(FileUtils.getExtension(url));
    if(dataType.charset().isEmpty() && dataType.primaryType().equals(DataTypes.TEXT)){
      dataType = dataType.withCharset(UTF_8);
    }
    final Bytes bytes = read(url);
    return createTyped(bytes, dataType);
  }

  public static BytesBuilder newBytesBuilder() {
    return new BytesBuilder();
  }

  public static BytesBuilder newBytesBuilder(final int initialBufferSize) {
    return new BytesBuilder(initialBufferSize);
  }

  public static Hash256 parseHash(final String hexString){
    return new Hash256Imp(hexToByteArray(hexString));
  }

  public static Bytes emptyBytes() {
    return ArrayBytes.EMPTY;
  }

  public static Bytes fromBigInteger(final BigInteger i) {
    return ArrayBytes.create(i.toByteArray());
  }

  public static Collector<Bytes,?,Bytes> joining(){
    return new JoiningBytesCollector();
  }

  public static char firstHex(final byte b) {
    return HEX_CHARS.charAt((b>>4) & 0xF);
  }

  public static char secondHex(final byte b) {
    return HEX_CHARS.charAt(b & 0xF);
  }

  public static Bytes parseBase64(final String base64) {
    return ArrayBytes.create(Base64.getDecoder().decode(base64));
  }

  static final TypedBytes createTyped(final Bytes content, final DataType dataType){
    final @Nullable Function<Bytes, TypedBytes> constructor = TYPED_CONSTRUCTORS.get(dataType.baseType());
    return constructor == null ? new DefaultTypedBytes(content, dataType) : constructor.apply(content);
  }

  private static final Map<DataType, Function<Bytes, TypedBytes>> createTypedConstructors() {
    final Map<DataType, Function<Bytes, TypedBytes>> map = new HashMap<>();
    map.put(Hash256.ALGORITHM.getDataType(), Hash256Imp::new);
    map.put(DataTypes.TEXT_PLAIN, PlainTextImp::new);
    map.put(DataTypes.MESSAGE_RFC822, EmailImp::new);
    return Collections.unmodifiableMap(map);
  }


}
