package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.Verify.verify;

import java.util.ArrayList;
import java.util.Iterator;

import com.github.gv2011.util.ann.NotThreadSafe;

@NotThreadSafe
public class ByteScanner implements Iterator<ByteIterator>{

  private final ByteIterator source;
  private final ByteIterator.Resettable pattern;
  private final Buffer buffer;

  private Part currentPart = new Part();
  private boolean isActive = false;
  private boolean hasNext = true;

  public ByteScanner(final ByteIterator source, final ByteIterator.Resettable pattern) {
    this.source = source;
    this.pattern = pattern;
    buffer = new Buffer();
  }

  @Override
  public boolean hasNext() {
    verify(!isActive);
    return hasNext;
  }

  @Override
  public ByteIterator next() {
    verify(hasNext());
    isActive = true;
    currentPart = new Part();
    buffer.reset();
    return currentPart;
  }

  private void deactivate(final Part part){
    assert part == currentPart && isActive && (buffer.isSequence() || !source.hasNext()) && part.consumed;
    hasNext = buffer.isSequence();
    isActive = false;
  }

  @NotThreadSafe
  private final class Part implements ByteIterator{

    private boolean consumed = false;

    @Override
    public boolean hasNext() {
      if(consumed) return false;
      else{
        final boolean hasNext = !buffer.isAtPartEnd();
        if(!hasNext){
          consumed = true;
          deactivate(this);
        }
        return hasNext;
      }
    }

    @Override
    public byte nextByte() {
      verify(hasNext());
      return buffer.advance();
    }

    @Override
    public Byte next() {
      return nextByte();
    }

    @Override
    public void close() {
      while(hasNext()) nextByte();
    }
  }


  @NotThreadSafe
  static interface ByteList{
    ByteIterator.Resettable resettableIterator();
    byte removeFirstByte();
    void add(byte nextByte);
    void clear();
    boolean isEmpty();
  }

  //Should be replaced with a more efficient implementation
  @NotThreadSafe
  static final class ArrayListByteList extends ArrayList<Byte> implements ByteList{
    @Override
    public ByteIterator.Resettable resettableIterator() {
      return new ByteIterator.Resettable(){
        private int i;
        @Override
        public boolean hasNext() {return i<size();}
        @Override
        public Byte next() {return nextByte();}
        @Override
        public byte nextByte() {return get(i++);}
        @Override
        public void close() {}
        @Override
        public void reset() {i=0;}
      };
    }

    public Byte removeFirst() {return removeFirstByte();}

    @Override
    public byte removeFirstByte() {
      return remove(0);
    }

    @Override
    public void add(final byte b) {add((Byte)b);}


  }


  @NotThreadSafe
  private final class Buffer{

    private final ByteList bufferList = new ArrayListByteList();
    private final ByteIterator.Resettable bufferIterator;
    private boolean isSequence;
    private boolean atEnd;

    private Buffer(){
      bufferIterator = bufferList.resettableIterator();
      isSequence = checkIfSequence();
    }

    private void reset() {
      isSequence = checkIfSequence();
    }

    private byte advance() {
      assert !isSequence();
      final byte result = bufferList.removeFirstByte();
      isSequence = checkIfSequence();
      return result;
    }

    private boolean isSequence(){
      return isSequence;
    }

    private boolean isAtPartEnd(){
      return isSequence || atEnd;
    }

    private boolean checkIfSequence(){
      boolean match = true;
      bufferIterator.reset();
      pattern.reset();
      while(match && pattern.hasNext()){
        if(!bufferIterator.hasNext()){
          //A further byte must be buffered:
          if(source.hasNext()) {
            //Append a new byte from the input stream to the end of the buffer.
            bufferList.add(source.nextByte());
          } else {
            //End of input, but end of pattern is missing -> no match
            match = false;
            atEnd = bufferList.isEmpty();
          }
        }
        if(match){
          match = bufferIterator.nextByte() == pattern.nextByte();
        }
      }
      if(match) bufferList.clear();
      return match;
    }
  }

}
