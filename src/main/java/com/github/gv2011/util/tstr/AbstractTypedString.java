package com.github.gv2011.util.tstr;

import static com.github.gv2011.util.ex.Exceptions.call;

import com.github.gv2011.util.beans.ParserClass;
import com.github.gv2011.util.icol.Opt;

public abstract class AbstractTypedString<T extends TypedString<T>>
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

  @SuppressWarnings("unchecked")
  public static final <T extends TypedString<T>> Opt<TypedStringParser<T>> getTypedStringParser(final Class<T> typedStringClass) {
    return
      Opt.ofNullable(typedStringClass.getAnnotation(ParserClass.class))
      .map(pc->(TypedStringParser<T>)call(()->pc.value().getConstructor().newInstance(new Object[0])))
    ;
  }


}
