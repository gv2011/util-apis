package com.github.gv2011.util.icol;

import java.util.Map.Entry;

final class DefaultListEntry<T> implements ListEntry<T>{

  private final IList<T> list;
  private final int index;


  DefaultListEntry(final IList<T> list, final int index) {
    this.list = list;
    this.index = index;
  }

  @Override
  public Integer getKey() {return index;}

  @Override
  public int index(){return index;}

  @Override
  public T getValue() {return list.get(index);}

  @Override
  public Opt<ListEntry<T>> tryGetPrevious() {
    return index==0 ? Opt.empty() : Opt.of(new DefaultListEntry<>(list, index-1));
  }

  @Override
  public Opt<ListEntry<T>> tryGetNext() {
    return index==0 ? Opt.empty() : Opt.of(new DefaultListEntry<>(list, index-1));
  }

  @Override
  public boolean equals(final Object other) {
    return other instanceof final Entry<?, ?> otherEntry  &&
      getKey().equals(otherEntry.getKey())                &&
      getValue().equals(otherEntry.getValue())
    ;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(index) ^ getValue().hashCode();
  }

  @Override
  public T setValue(final T value) {
    throw new UnsupportedOperationException();
  }

}
