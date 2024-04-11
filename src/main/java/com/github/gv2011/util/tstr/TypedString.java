package com.github.gv2011.util.tstr;

import java.util.Comparator;

import com.github.gv2011.util.Equal;
import com.github.gv2011.util.beans.BeanHashCode;

public interface TypedString<T extends TypedString<T>> extends CharSequence, Comparable<TypedString<?>>{

  @FunctionalInterface
  public static interface TypedStringParser<T extends TypedString<T>>{
    T parse(String s);
  }

  public static <T extends TypedString<T>> T create(final Class<T> clazz, final String value) {
    return TypedStringInvocationHandler.create(clazz, value);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static final Comparator<TypedString<?>> COMPARATOR = (s1,s2)->{
    int result;
    if(s1==s2) result = 0;
    else {
      result = s1.clazz().getName().compareTo(s2.clazz().getName());
      if(result==0) {
        result = ((TypedString)s1).compareWithOtherOfSameType((s2));
      }
    }
    return result;
  };

  public static int hashCode(final TypedString<?> s) {
      return hashCode(s.clazz(), s.canonical());
    }

    public static int hashCode(final Class<? extends TypedString<?>> clazz, final String canonical) {
      return BeanHashCode.classHashCode(clazz) * 31 + canonical.hashCode();
    }

    public static boolean equal(final TypedString<?> s, final Object obj) {
      return s==obj ? true
        : Equal.equal(s, obj, TypedString.class, o->{
          return s.clazz().equals(o.clazz()) && s.toString().equals(o.toString());
        })
      ;
    }


  T self();

  Class<T> clazz();

  @Override
  default boolean isEmpty() {
      return canonical().isEmpty();
  }

  default String canonical() {
    return toString();
  }

  default int compareWithOtherOfSameType(final T o) {
    return canonical().compareTo(o.canonical());
  }

  @Override
  default int compareTo(final TypedString<?> o) {
    return COMPARATOR.compare(this, o);
  }

  @Override
  default int length() {
    return canonical().length();
  }

  @Override
  default char charAt(final int index) {
    return canonical().charAt(index);
  }

  @Override
  default CharSequence subSequence(final int start, final int end) {
    return canonical().subSequence(start, end);
  }


}
