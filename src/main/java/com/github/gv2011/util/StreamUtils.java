package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.function.IntConsumer;

import com.github.gv2011.util.ex.ThrowingSupplier;

public final class StreamUtils {

  private StreamUtils(){staticClass();}

  public static byte[] readBytes(final InputStream in, final int length){
    return call(()->{
      final byte[] result = new byte[length];
      int sum = in.read(result);
      if(sum==-1) throw new IllegalStateException("Premature end of stream.");
      while(sum<length){
        final int read = in.read(result, sum, length-sum);
        if(read==-1) throw new IllegalStateException("Premature end of stream.");
        sum+=read;
      }
      return result;
    });
  }

  public static String readText(final ThrowingSupplier<InputStream> in){
    return StringUtils.read(()->new InputStreamReader(in.get(), CharsetUtils.utf8Decoder()));
  }

  public static byte[] readAndClose(final ThrowingSupplier<InputStream> in){
    return callWithCloseable(in, s->{
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      final byte[] buffer = new byte[1024];
      int read = s.read(buffer);
      while(read!=-1){
        bos.write(buffer, 0, read);
        read = s.read(buffer);
      }
      return bos.toByteArray();
    });
  }

  public static long countAndClose(final ThrowingSupplier<InputStream> in){
    return callWithCloseable(in, s->{
      return count(s);
    });
  }

  public static long count(final InputStream s){
    return call(()->{
      long count = s.skip(Long.MAX_VALUE);
      int eof = s.read();
      while(eof!=-1){
        count++;
        count += s.skip(Long.MAX_VALUE);
        eof = s.read();
      }
      return count;
    });
  }

  public static long copy(final ThrowingSupplier<InputStream> in, final OutputStream out) {
    final byte[] buffer = new byte[1024];
    return callWithCloseable(in, s->{
      long size=0;
      int count = s.read(buffer);
      while(count!=-1){
        out.write(buffer, 0, count);
        size += count;
        count = s.read(buffer);
      }
      return size;
    });
  }

  public static CloseableIntIterator asIterator(final InputStream in) {
    return new StreamIterator(in);
  }

  public static final InputStream countingStream(final InputStream in, final IntConsumer counter){
    return countingStream(in, counter, i->i);
  }

  public static final OutputStream countingStream(final OutputStream out, final IntConsumer counter){
    return new FilterOutputStream(out){
      @Override
      public void write(final int b) throws IOException {
        out.write(b);
        counter.accept(1);
      }
      @Override
      public void write(final byte[] b, final int off, final int len) throws IOException {
        out.write(b, off, len);
        counter.accept(len);
      }
    };
  }

  @FunctionalInterface
  public static interface Throttler{
    /**
     * @param limit never less than 1
     * @return int between 1 and limit
     */
    int maxReadCount(int limit);
  }

  public static final InputStream countingStream(
    final InputStream in, final IntConsumer counter, final Throttler throttler
  ){
    return new InputStream(){
      @Override
      public int read() throws IOException {
        throttler.maxReadCount(1);
        final int next = in.read();
        if(next!=-1) counter.accept(1);
        return next;
      }
      @Override
      public int read(final byte[] b, final int off, final int len) throws IOException {
        final int count = in.read(b, off, throttler.maxReadCount(len));
        if(count!=-1) counter.accept(count);
        return count;
      }
      @Override
      public void close() throws IOException {
        in.close();
      }
    };
  }
}
