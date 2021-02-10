package com.github.gv2011.util.uc;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

import com.github.gv2011.util.CloseableIntIterator;

public final class UChars {

  private static final UStrFactory FACTORY = new UStrFactoryImp();
  private static final Utf8Decoder UTF8_DECODER = new Utf8Decoder();

  public static UStrFactory uStrFactory(){
    return FACTORY;
  }

  public static UStr collect(final int size, final IntUnaryOperator valueForIndex) {
    return uStrFactory().collect(size, valueForIndex);
  }

  public static UChar uChar(final int codePoint) {
    return uStrFactory().uChar(codePoint);
  }

  public static UChar uChar(final String character) {
    return uStrFactory().uChar(character);
  }

  public static UStr toUStr(final int[] codepoints) {
    return collect(codepoints.length, i->codepoints[i]);
  }

  public static final String toString(final int[] codepoints) {
    return Arrays.stream(codepoints).mapToObj(UChars::toString).collect(joining());
  }

  public static final String toString(final int codepoint) {
    return uChar(codepoint).toString();
  }

  public static boolean isSurrogate(final int codepoint) {
    return UCharImp.isSurrogate(codepoint);
  }

  public static int toCodepoint(final String str) {
    return UCharImp.toCodepoint(str);
  }

  public static UStr uStr(final String str) {
    return FACTORY.uStr(str);
  }

  public static IntStream decode(final CloseableIntIterator utf8){
    return UTF8_DECODER.decode(utf8);
  }

}
