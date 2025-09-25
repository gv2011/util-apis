package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.CollectionUtils.tryGet;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.wrap;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;

import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.num.NumUtils;

class CachedFileBytes extends AbstractBytes{

  private static final Logger LOG = getLogger(CachedFileBytes.class);

  private static final int BLOCK_SIZE = 65536;

  private final long fileSize;
  private final FileTime lastModified;
  private final Path file;
  private final long offset;
  private final long size;

  private final Object lock = new Object();
  private SoftReference<Map<Long,SoftReference<byte[]>>> cache = new SoftReference<>(new HashMap<>());

  CachedFileBytes(final Path file) {
    verify(file, Files::isRegularFile);
    final BasicFileAttributes atts = call(()->
      Files.getFileAttributeView(file, BasicFileAttributeView.class).readAttributes()
    );
    lastModified = atts.lastModifiedTime();
    fileSize = atts.size();
    this.file = file;
    offset = 0;
    size = fileSize;
  }

  CachedFileBytes(final Path file, final long offset, final long size) {
    verify(file, Files::isRegularFile);
    lastModified = call(()->Files.getLastModifiedTime(file));
    fileSize = call(()->Files.size(file));
    this.file = file;
    this.offset = offset;
    this.size = size;
  }

  protected final Path file(){
    return file;
  }

  @Override
  public final long longSize() {
    checkNotClosed();
    return size;
  }

  @Override
  public final byte get(final long index) {
    checkNotClosed();
    final int i = NumUtils.toInt(index % (long) BLOCK_SIZE);
    return getBlockForIndex(index)[i];
  }

  private byte[] getBlockForIndex(final long index){
    if(index<0) throw new IndexOutOfBoundsException(format("{} < 0.", index));
    if(index>=size) throw new IndexOutOfBoundsException(format("{} is greater or equal size {}.", index, size));
    final long block = index / (long) BLOCK_SIZE;
    final byte[] result = tryGet(cache(), block)
      .flatMap(r->{
        final Opt<byte[]> cached = Opt.ofNullable(r.get());
        if(cached.isEmpty()) LOG.info("Reloading.");
        return cached;
      })
      .orElseGet(()->{
        return loadBlock(block);
      })
    ;
    assert result.length==0 ? index == longSize() : true;
    return result;
  }

  private Map<Long,SoftReference<byte[]>> cache(){
    synchronized(lock){
      return Optional.ofNullable(cache.get()).orElseGet(()->{
        final Map<Long,SoftReference<byte[]>> result = new HashMap<>();
        cache = new SoftReference<>(result);
        return result;
      });
    }
  }

  private byte[] loadBlock(final long block) {
    final long start = block * (long) BLOCK_SIZE;
    final long end = Math.min(start + (long) BLOCK_SIZE, size);
    final byte[] result = new byte[NumUtils.toInt(end-start)];
    try(InputStream stream = openStream()){
      stream.skip(start);
      int count = 0;
      while(count<result.length){
        final int c = stream.read(result, count, result.length-count);
        if(c<1){
          if(c==-1) throw new IllegalStateException("Premature end of stream.");
          verify(false);
        }
        count += c;
      }
    }
    catch (final IOException e) {throw wrap(e);}
    synchronized(lock){
      cache().put(block, new SoftReference<>(result));
    }
    return result;
  }

  @Override
  public final byte[] toByteArray() {
    final byte[] result = new byte[size()];
    try(DataInputStream din = new DataInputStream(openStream())){
      din.readFully(result);
    }catch(final IOException e){throw wrap(e);}
    return result;
  }

  @Override
  public final ByteIterator.Resettable iterator() {
    checkNotClosed();
    return new StreamIterator.Resettable(()->openStream());
  }

  @Override
  public final CachedFileBytes subList(final long fromIndex, final long toIndex) {
    final long size = longSize();
    checkIndices(fromIndex, toIndex, size);
    if(fromIndex==0 && toIndex==size) return this;
    else{
      return new CachedFileBytes(file(), offset+fromIndex, toIndex-fromIndex);
    }
  }

  @Override
  public final InputStream openStream() {
    checkNotClosed();
    verifyEqual(call(()->Files.size(file)), fileSize);
    verifyEqual(call(()->Files.getLastModifiedTime(file)), lastModified);
    return new TruncatedStream(call(()->Files.newInputStream(file())), offset, size);
  }

  @Override
  public int write(final ByteBuffer buffer, final long offset){
    checkNotClosed();
    if(offset==longSize()) return 0;
    else{
      final byte[] block = getBlockForIndex(offset);
      final int i = NumUtils.toInt(offset % (long) BLOCK_SIZE);
      assert 0<=i && i<=block.length;
      final int length = Math.min(block.length-i, buffer.remaining());
      assert
        (length==0 ? buffer.remaining()==0 : true) &&
        0<=length && length <= block.length-i && length <= buffer.remaining()
      ;
      buffer.put(block, i, length);
      return length;
    }
  }

  @Override
  public ByteBuffer toBuffer(final long offset) {
    final ByteBuffer buffer;
    final int length;
    if(offset==longSize()){
      buffer = ByteBuffer.allocate(0).asReadOnlyBuffer();
      length = 0;
    }
    else{
      final byte[] block = getBlockForIndex(offset);
      final int i = NumUtils.toInt(offset % (long) BLOCK_SIZE);
      length = block.length-i;
      assert
        (length==0 ? offset==longSize() : true) &&
        0<=length && length <= block.length-i
      ;
      buffer = ByteBuffer.wrap(block, i, length).asReadOnlyBuffer();
    }
    assert buffer.remaining() == length;
    return buffer;
  }
}
