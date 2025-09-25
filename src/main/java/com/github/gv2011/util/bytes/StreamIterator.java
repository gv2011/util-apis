package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.ex.Exceptions.call;

import java.io.InputStream;
import java.util.NoSuchElementException;

import com.github.gv2011.util.ex.ThrowingSupplier;

public abstract class StreamIterator implements ByteIterator{

  private int next;

  StreamIterator() {
    this(true);
  }

  StreamIterator(final boolean readNext) {
    if(readNext)readNext();
  }

  abstract InputStream stream();

  final void readNext() {
    next = call(()->stream().read());
    if(!hasNext()) close();
  }

  @Override
  public void close() {
    call(()->stream().close());
  }

  @Override
  public boolean hasNext() {
    return next!=-1;
  }

  @Override
  public byte nextByte() {
    if(next==-1)throw new NoSuchElementException();
    final byte result = (byte)next;
    readNext();
    return result;
  }

  @Override
  public Byte next() {
    return nextByte();
  }

  static final class Default extends StreamIterator{
    private final InputStream stream;
    Default(final InputStream stream) {
      this.stream = notNull(stream);
    }
    @Override
    InputStream stream() {
      return stream;
    }
  }

  static final class Resettable extends StreamIterator implements ByteIterator.Resettable{
    private final ThrowingSupplier<InputStream> streamSupplier;
    private volatile InputStream stream;
    Resettable(final ThrowingSupplier<InputStream> streamSupplier) {
      super(false);
      this.streamSupplier = streamSupplier;
      this.stream = notNull(streamSupplier.get());
      readNext();
    }
    @Override
    public void reset() {
      close();
      stream = streamSupplier.get();
    }
    @Override
    InputStream stream() {
      return stream;
    }
  }

}
