package com.github.gv2011.util.tstr;

public abstract class AbstractTypedString<T extends AbstractTypedString<T>>
implements TypedString<T>{

  @Override
  public abstract String toString();

  @Override
  public int hashCode(){
    return TypedString.hashCode(this);
  }

  @Override
  public boolean equals(final Object obj) {
    return TypedString.equal(this, obj);
  }

  @Override
  public final int compareTo(final TypedString<?> o) {
    return COMPARATOR.compare(this, o);
  }

  @Override
  public int length() {
    return canonical().length();
  }

  @Override
  public char charAt(final int index) {
    return canonical().charAt(index);
  }

  @Override
  public CharSequence subSequence(final int start, final int end) {
    return canonical().subSequence(start, end);
  }

}
