package com.github.gv2011.util.streams;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.function.Supplier;

import com.github.gv2011.util.icol.Opt;

public class InputStreamChannel implements SeekableByteChannel{
  
  private final Object lock = new Object();
  
  private final Supplier<InputStream> supplier;
  private final long size;
  private final byte[] buffer = new byte[4096];
  
  private boolean open = true;
  private Opt<InputStream> in = Opt.empty();
  private long position = 0;
  private long streamPosition = 0;
  
  public InputStreamChannel(Supplier<InputStream> supplier, long size) {
    this.supplier = supplier;
    this.size = size;
  }

  @Override
  public boolean isOpen() {
    synchronized(lock){return open;}
  }

  @Override
  public void close(){
    synchronized(lock){
      open = false;
      closeStream();
    }
  }

  private void closeStream() {
    assert Thread.holdsLock(lock);
    if(in.isPresent()){
      call(in.get()::close);
      in = Opt.empty();
      streamPosition = 0;
    }
  }

  @Override
  public int read(ByteBuffer dst) throws IOException {
    synchronized(lock){
      checkOpen();
      if(dst.remaining()==0) return 0;
      else{
        if(position>=size) return -1;
        else{
          if(position<streamPosition){
            closeStream();
          }
          while(streamPosition<position){
            long skipped = call(()->in().skip(position-streamPosition));
            if(skipped==0){
              checkNotEndOfStream(in().read());
              streamPosition++;
            }
            streamPosition += skipped;
          }
          assert streamPosition==position;
          final int maxCount = Math.min((int) Math.min((long) dst.remaining(), size-position), buffer.length);
          final int count = in().read(buffer, 0, maxCount);
          checkNotEndOfStream(count);
          if(count>0){
            dst.put(buffer, 0, count);
            position += count;
            streamPosition = position;
          }
          return count;
        }
      }
    }
  }

  private void checkNotEndOfStream(final int count) throws IOException {
    if(count<0){
      close();
      throw new IOException("Premature end of stream.");
    }
  }
  
  
  private InputStream in(){
    assert Thread.holdsLock(lock) && position<size;
    return in.orElseGet(()->{
      final InputStream s = supplier.get();
      call(()->s.skip(position));
      in = Opt.of(s);      
      return s;
    });
  }

  private void checkOpen() {
    assert Thread.holdsLock(lock);
    verify(open);
  }

  @Override
  public int write(ByteBuffer src) throws IOException {
    throw new IOException("readonly");
  }

  @Override
  public long position() throws IOException {
    synchronized(lock){
      checkOpen();
      return position;
    }
  }

  @Override
  public SeekableByteChannel position(long newPosition) throws IOException {
    verify(position>=0);
    synchronized(lock){
      checkOpen();
      this.position=newPosition;
    }
    return this;
  }

  @Override
  public long size() throws IOException {
    return size;
  }

  @Override
  public SeekableByteChannel truncate(long size) throws IOException {
    throw new IOException("readonly");
  }

}
