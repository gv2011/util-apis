package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;
import static com.github.gv2011.util.ex.Exceptions.wrap;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.AbstractList;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.HashAlgorithm;
import com.github.gv2011.util.HashUtils;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.StreamUtils;
import com.github.gv2011.util.ann.Immutable;
import com.github.gv2011.util.num.NumUtils;
import com.github.gv2011.util.uc.UChars;
import com.github.gv2011.util.uc.UStr;


@Immutable
public abstract class AbstractBytes extends AbstractList<Byte> implements Bytes{

  private static final Logger LOG = getLogger(AbstractBytes.class);

  private final Constant<Integer> hashCodeCache = Constants.cachedConstant(super::hashCode);
  private final Constant<String> toStringCache = Constants.softRefConstant(this::toStringImp);
  private final Constant<HashAndSize> hashCache = Constants.cachedConstant(this::hashImp);


  @Override
  public final String toString(){
    return toStringCache.get();
  }

  protected final void checkNotClosed() {
    if(closed()) throw new IllegalStateException("Closed.");
  }

  protected String toStringImp(){
    final int s = size();
    final char[] result = new char[s==0?0:s*3-1];
    int i=0;
    for(final byte b: this){
      result[i*3] = ByteUtils.firstHex(b);
      result[i*3+1] = ByteUtils.secondHex(b);
      if(i<s-1)result[i*3+2] = ' ';
      i++;
    }
    return new String(result);
  }

  @Override
  public int size() {
    final long size = longSize();
    if(size>Integer.MAX_VALUE) throw new TooBigException();
    return (int)size;
  }

  @Override
  public final Byte get(final int index) {
    return getByte(index);
  }

  @Override
  public byte getByte(final int index){
    return get((long)index);
  }

  @Override
  public int getUnsigned(final long index) {
    return Byte.toUnsignedInt(get(index));
  }

  @Override
  public byte[] toByteArray(){
    final byte[] result = new byte[size()];
    int i=0;
    for(final byte b: this) result[i++]=b;
    return result;
  }

  @Override
  public Bytes subList(final int fromIndex){
    return subList(fromIndex, longSize());
  }

  @Override
  public Bytes subList(final int fromIndex, final int toIndex){
    return subList((long)fromIndex, (long)toIndex);
  }

  @Override
  public Pair<Bytes,Bytes> split(final long index){
    final Bytes b1 = this.subList(0L, index);
    final Bytes b2 = this.subList(index, longSize());
    return pair(b1, b2);
  }



  @Override
  public int write(final byte[] b, final int off, final int len) {
    final int result = (int) Math.min(len, longSize());
    for(int i=0; i<result; i++){b[off+i] = getByte(i);}
    return result;
  }

  @Override
  public void write(final OutputStream stream){
    call(()->{
      for(final byte b: this) stream.write(b);
    });
  }


  @Override
  public final void write(final Path file) {
    write(file, false);
  }

  @Override
  public final void write(final Path file, final boolean onlyOwner) {
    checkNotClosed();
    if(onlyOwner){
      try {
        Files.createFile(file);
      } catch (final FileAlreadyExistsException e) {
      } catch (final IOException e) {
        throw wrap(e);
      }
      try {
        Files.setPosixFilePermissions(file, EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE));
      } catch (final UnsupportedOperationException e) {
        LOG.warn(format("Cannot set only user can read permissions on file {}.", file), e);
      } catch (final IOException e) {
        throw wrap(e);
      }
    }
    try(OutputStream stream = Files.newOutputStream(file, CREATE, TRUNCATE_EXISTING)) {
      write(stream);
    } catch (final IOException e) {throw wrap(e);}
  }

  @Override
  public int hashCode() {
     return hashCodeCache.get();
  }

  @Override
  public final String utf8ToString() throws TooBigException {
    return StreamUtils.readText(this::openStream);
  }


  @Override
  public UStr utf8ToUStr() throws TooBigException {
    return UChars.uStr(utf8ToString());
  }

  @Override
  public String toString(final Charset charset) {
    return new String(toByteArray(), charset);
  }


  @Override
  public final Hash256 hash() {
    return hashAndSize().hash();
  }

  @Override
  public final HashAndSize hashAndSize() {
    return hashCache.get();
  }

  @Override
  public final Hash hash(final HashAlgorithm hashAlgorithm) {
    checkNotClosed();
    return HashUtils.hash(hashAlgorithm, this::openStream);
  }

  @Override
  public boolean equals(final Object o) {
    boolean result;
    if(o==this) result = true;
    else if(!(o instanceof Bytes)) result = listEquals(o);
    else{
      final Bytes other = (Bytes)o;
      final long size = longSize();
      if(size!=other.longSize()) result = false;
      else if(size<=Hash256.SIZE) result = listEquals(o);
      else result = hash().equals(other.hash());
    }
    return result;
  }

  protected final boolean listEquals(final Object o) {
    boolean result;
    if(o==this) result = true;
    else if(!(o instanceof List)) result = false;
    else{
      final List<?> other = (List<?>)o;
      final long size = longSize();
      if(size!=other.size()) result = false;
      else result = super.equals(o);
    }
    return result;
  }

  protected HashAndSize hashImp() {
    checkNotClosed();
    return HashUtils.hash256(this::openStream, OutputStream.nullOutputStream());
  }


  @Override
  public Hash256 asHash() {
    return new Hash256Imp(toByteArray());
  }

  @Override
  public final Reader reader() {
	return new InputStreamReader(openStream(), UTF_8);
  }

  @Override
  public int toInt() {
    final int size = size();
    if(size>4) throw new IllegalStateException();
    final boolean negative = size==0?false:getByte(0)<0;
    int result = negative?-1:0;
    for(final byte b: this){
      result = ((result<<8) & -0x100) | (b & 0xFF);
    }
    return result;
  }

  @Override
  public final long toLong() {
    final int size = size();
    if(size>8) throw new IllegalStateException();
    final boolean negative = size==0?false:getByte(0)<0;
    long result = negative?-1L:0L;
    for(final byte b: this){
      result = ((result<<8) & -0x100L) | (b & 0xFFL);
    }
    return result;
  }



  @Override
  public BigInteger toBigInteger() {
    return new BigInteger(1, toByteArray());
  }

  @Override
  public Bytes toBase64() {
    checkNotClosed();
    try(final BytesBuilder builder = ByteUtils.newBytesBuilder()){
      final OutputStream stream = Base64.getEncoder().wrap(builder);
      write(stream);
      call(stream::close);
      return builder.build();
      }
  }

  @Override
  public String toBase64String() {
    checkNotClosed();
    return callWithCloseable(ByteArrayOutputStream::new, bos->{
      final OutputStream stream = Base64.getEncoder().wrap(bos);
      write(stream);
      call(stream::close);
      return bos.toString(UTF_8);
    });
  }

  @Override
  public Bytes decodeBase64() {
    try(final InputStream stream = Base64.getDecoder().wrap(openStream())){
      return ByteUtils.fromStream(stream);
    } catch (final IOException e) {throw wrap(e);}
  }

  @Override
  public Iterator<Byte> iterator() {
    checkNotClosed();
    return new It(0);
  }

  @Override
  public ListIterator<Byte> listIterator(final int index) {
    checkNotClosed();
    return new It(index);
  }

  protected final static void checkIndices(final long fromIndex, final long toIndex, final long size) {
    if(
      fromIndex>size ||
      toIndex>size   ||
      fromIndex < 0  ||
      toIndex < 0
    ) throw new IndexOutOfBoundsException();
    if(fromIndex>toIndex) throw new IllegalArgumentException();
  }

  @Override
  public String toHex() {
    final StringBuilder sb = new StringBuilder();
    for(int i=0; i<size(); i++){
      sb.append(toHex(getUnsigned(i)));
    }
    return sb.toString();
  }

  @Override
  public String toHexMultiline() {
    final StringBuilder sb = new StringBuilder();
    int column = 0;
    for(int i=0; i<size(); i++){
      if(column>0){
        if(column==32){
          sb.append('\n');
          column = 0;
        }
        else sb.append(' ');
      }
      sb.append(toHex(getUnsigned(i)));
      column++;
    }
    return sb.toString();
  }

  @Override
  public final String toHexColon() {
    final StringBuilder sb = new StringBuilder();
    if(!isEmpty()) {
    	sb.append(toHex(getUnsigned(0)));
	    for(int i=1; i<size(); i++){
	      sb.append(':');
	      sb.append(toHex(getUnsigned(i)));
	    }
    }
    return sb.toString();
  }

  private String toHex(final int b) {
    if(b<0x10) return "0"+Integer.toHexString(b);
    else return Integer.toHexString(b);
  }

  @Override
  public Bytes append(final Bytes hashBytes) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public boolean startsWith(final Bytes prefix) {
    return startsWith(prefix, 0);
  }

  private boolean startsWith(final Bytes prefix, final long offset) {
    checkNotClosed();
    if(prefix.isEmpty()) return true;
    else if(prefix.longSize()>longSize()-offset) return false;
    else{
      boolean result = true;
      long i=0;
      while(result && i<prefix.longSize()){
        if(get(offset+i)!=prefix.get(i)) result = false;
        i++;
      }
      return result;
    }
  }

  @Override
  public Optional<Long> indexOfOther(final Bytes other) {
    checkNotClosed();
    Optional<Long> result = Optional.empty();
    boolean done = false;
    long searchIndex = 0;
    long remainingSize = longSize();
    final long otherSize = other.longSize();
    while(!done){
      if(remainingSize<otherSize){done = true;}
      else if(startsWith(other, searchIndex)){
        result = Optional.of(searchIndex);
        done = true;
      }
      else {searchIndex++; remainingSize--;}
    }
    return result;
  }


  @Override
  public int compareTo(final Bytes o) {
    if(equals(o)) return 0;
    else {
      int result = 0;
      long i = 0;
      while(result==0 && i<longSize() && i<o.longSize()) {
        result = getUnsigned(i)-o.getUnsigned(i);
        i++;
      }
      if(result==0) result = Long.signum(longSize()-o.longSize());
      assert result!=0;
      return result;
    }
  }


  private final class It implements ListIterator<Byte> {
    private long index;
    private It(final long index) {
      this.index = index;
    }
    @Override
    public boolean hasNext() {
      return index<longSize();
    }
    @Override
    public Byte next() {
      try {return get(index++);}
      catch (final IndexOutOfBoundsException e) {
        throw new NoSuchElementException();
      }
    }
    @Override
    public boolean hasPrevious() {
      return index>0;
    }
    @Override
    public Byte previous() {
      if(index==0)throw new NoSuchElementException();
      return get(--index);
    }
    @Override
    public int nextIndex() {
      final long next = index+1;
      if(next>Integer.MAX_VALUE) throw new TooBigException();
      return (int)next;
    }
    @Override
    public int previousIndex() {
      final long previous = index-1;
      if(previous>Integer.MAX_VALUE) throw new TooBigException();
      return (int)previous;
    }
    @Override
    public void remove() {
      throw new UnsupportedOperationException("Read-only");
    }
    @Override
    public void set(final Byte e) {
      throw new UnsupportedOperationException("Read-only");
    }
    @Override
    public void add(final Byte e) {
      throw new UnsupportedOperationException("Read-only");
    }
  }


  @Override
  public TypedBytes typed() {
    return typed(DataTypes.APPLICATION_OCTET_STREAM);
  }

  @Override
  public TypedBytes typed(final DataType dataType) {
    return ByteUtils.createTyped(this, dataType);
  }

  @Override
  public ByteBuffer toBuffer(final long offset, final int size) {
    return ByteBuffer.wrap(subList(offset, offset+size).toByteArray()).asReadOnlyBuffer();
  }

  @Override
  public ByteBuffer toBuffer(final long offset) {
    return toBuffer(offset, NumUtils.toInt(Math.min(longSize()-offset, 4096)));
  }

  @Override
  public int write(final ByteBuffer buffer, final long offset){
    verify(offset>=0 && offset<=longSize());
    final int count = NumUtils.toInt(Math.min(longSize()-offset, buffer.remaining()));
    for(int i=0; i<count; i++){
      buffer.put(get(offset+i));
    }
    return count;
  }

}
