package com.github.gv2011.util;

import static com.github.gv2011.util.CollectionUtils.collectToString;
import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.CollectionUtils.toSortedSet;
import static com.github.gv2011.util.CollectionUtils.tryGet;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.icol.ICollections.listBuilder;
import static com.github.gv2011.util.icol.ICollections.toIList;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;

import java.io.Reader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.ex.ThrowingSupplier;
import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IList.Builder;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.uc.SetOfChars;

public final class StringUtils {

  private StringUtils(){staticClass();}

  public static final Pattern WHITESPACE = Pattern.compile("\\s+");

  private static final Map<Integer,int[]> UMLAUTE_TRANSLITERATION = umlauteTransliteration();

  public static String removeWhitespace(final String s) {
    return s.replaceAll("\\s+", "");
  }

  public static String removeTail(final String s, final String tail) {
    if(!s.endsWith(tail)) throw new IllegalArgumentException(format("{} does not end with {}.", s, tail));
    return s.substring(0, s.length()-tail.length());
  }

  public static String lastPart(final String s) {
    return lastPart(s, '.');
  }

  public static String lastPart(final String s, final char c) {
    final int i = s.lastIndexOf(c);
    return i==-1?s:s.substring(i+1);
  }

  public static String removePrefix(final String s, final String prefix) {
    if(!s.startsWith(prefix)) throw new IllegalArgumentException(
      format("{} does not start with {}.", s, prefix)
    );
    return s.substring(prefix.length());
  }

  public static String toLowerCase(final String s) {
    return s.toLowerCase(Locale.ROOT);
  }

  public static String toUpperCase(final String s) {
    return s.toUpperCase(Locale.ROOT);
  }

  public static Opt<String> tryRemovePrefix(final String s, final String prefix) {
    if(!s.startsWith(prefix)) return Opt.empty();
    else return Opt.of(s.substring(prefix.length()));
  }

  public static Function<String,Stream<String>> tryRemovePrefix(final String prefix) {
    final int length = prefix.length();
    return s->s.startsWith(prefix) ? Stream.of(s.substring(length)) : Stream.empty();
  }

  public static String tryRemoveTail(final String s, final String tail) {
    if(s.endsWith(tail)) return s.substring(0, s.length()-tail.length());
    else return s;
  }

  public static Opt<String> withoutTail(final String s, final String tail) {
    if(s.endsWith(tail)) return Opt.of(s.substring(0, s.length()-tail.length()));
    else return Opt.empty();
  }

  public static String showSpecial(final String s) {
    final StringBuilder result = new StringBuilder();
    for(int i=0; i<s.length(); i++){
      final char c = s.charAt(i);
      if(c<' ' || c>'~' || c=='[' || c=='\\' || c=='"'){
        result.append('[').append(Integer.toHexString(c)).append(']');
        if(c=='\n') result.append('\n');
      }
      else result.append(c);
    }
    return result.toString();
  }

  public static String fromSpecial(final String s) {
    final StringBuilder result = new StringBuilder();
    int i=0;
    while(i<s.length()){
      char c = s.charAt(i);
      if(c=='['){
        final StringBuilder hex = new StringBuilder();
        c = s.charAt(++i);
        while(c!=']'){
          hex.append(c);
          i++;
          verify(i<s.length());
          c = s.charAt(i);
        }
        final int decoded = Integer.parseInt(hex.toString(), 16);
        verify(decoded>=Character.MIN_VALUE && decoded<=Character.MAX_VALUE);
        result.append((char)decoded);
      }
      else if(c!='\n'){
        result.append(c);
      }
      i++;
    }
    return result.toString();
  }

  public static String multiply(final CharSequence str, final int factor) {
    if (factor < 0) throw new IllegalArgumentException();
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < factor; i++) sb.append(str);
    return sb.toString();
  }

  public static String multiply(final char c, final int factor) {
    return new String(fillArray(c, factor));
  }

  public static char[] fillArray(final char c, final int length) {
    final char[] chars = new char[length];
    Arrays.fill(chars, c);
    return chars;
  }

  public static String alignRight(final Object str, final int size) {
    return alignRight(str.toString(), size, ' ');
  }

  public static String alignLeft(final Object str, final int size) {
    return alignLeft(str.toString(), size, ' ');
  }

  public static Opt<Integer> firstDifference(final String s1, final String s2) {
    final int min = Math.min(s1.length(), s2.length());
    return Opt
      .ofOptional(
        IntStream.range(0, min)
        .filter(i->s1.charAt(i)!=s2.charAt(i))
        .findFirst()
      )
      .or(()->s1.length()==s2.length() ? Opt.empty() : Opt.of(min))
    ;
  }

  public static String alignRight(final CharSequence str, final int size, final char fill) {
    final int fillSize = size - str.length();
    if (fillSize < 0) throw new IllegalArgumentException(format("The size of \"{}\" is bigger than {}.", str, size));
    return new StringBuilder(size).append(fillArray(fill, fillSize)).append(str).toString();
  }

  public static String alignLeft(final CharSequence str, final int size, final char fill) {
    final int fillSize = size - str.length();
    if (fillSize < 0) throw new IllegalArgumentException("Does not fit.");
    return new StringBuilder(size).append(str).append(fillArray(fill, fillSize)).toString();
  }

  public static SortedSet<String> splitToSet(final String str) {
    return Arrays.stream(str.split(Pattern.quote(",")))
      .map(String::trim)
      .filter(s->!s.isEmpty())
      .collect(toSortedSet())
    ;
  }

  public static IList<String> split(final String text, final char c) {
    final Builder<String> listBuilder = listBuilder();
    addSplit(text, c, listBuilder);
    return listBuilder.build();
  }

  public static IList<String> split(final String text, final String separator) {
    return ICollections.asList(text.split(Pattern.quote(separator)));
  }

  public static IList<String> words(final String text) {
    return Arrays.stream(WHITESPACE.split(text))
      .map(String::trim)
      .filter(not(String::isEmpty))
      .collect(toIList());
  }

  public static String replaceUmlauts(final String text) {
    return toString(text.codePoints()
      .flatMap(i->
        tryGet(UMLAUTE_TRANSLITERATION, i)
        .map(IntStream::of)
        .orElseGet(()->IntStream.of(i))
      )
    );
  }

  public static void addSplit(final String text, final char c, final Builder<String> listBuilder) {
    int i = text.indexOf(c);
    int from = 0;
    while(i!=-1) {
      listBuilder.add(text.substring(from, i));
      from = i+1;
      i = text.indexOf(c, from);
    }
    listBuilder.add(text.substring(from));
  }

  public static boolean isTrimmed(final String s) {
    return s.trim().equals(s);
  }

  public static boolean isLowerCase(final String s) {
    return toLowerCase(s).equals(s);
  }

  public static final TypedBytes toUtf8(final String s){
    return ByteUtils.asUtf8(s);
  }

  public static final String toString(final IntStream codePoints){
      return codePoints
        .collect(
          StringBuilder::new,
          StringBuilder::appendCodePoint,
          StringBuilder::append
        )
        .toString()
      ;
  }

  public static final String read(final ThrowingSupplier<Reader> reader){
    return callWithCloseable(reader, r->{
      final StringWriter sw = new StringWriter();
      r.transferTo(sw);
      return sw.toString();
    });
  }

  public static final String randomString(final Random random, final int cpLength){
    final SetOfChars selection = SetOfChars.Selection.SIMPLE_LATIN.set();
    return collectToString(
      random.ints(0, Character.MAX_CODE_POINT+1)
      .filter(selection::containsChar)
      .limit(cpLength)
    );
  }


  private static Map<Integer, int[]> umlauteTransliteration() {
    return Collections.unmodifiableMap(
      Stream.of(pair('Ä',"AE"), pair('Ö',"OE"), pair('Ü',"UE"), pair('ẞ',"SS"))
      .flatMap(p->Stream.of(
        p,
        pair(Character.toLowerCase(p.getKey()), p.getValue().toLowerCase(Locale.GERMANY))
      ))
      .collect(toMap(
        p->verify(Character.toString(p.getKey()).codePoints().toArray(), a->a.length==1)[0],
        p->p.getValue().codePoints().toArray()
      ))
    );
  }

  public static Opt<String> nonEmpty(final String string) {
    return string.isEmpty() ? Opt.empty() : Opt.of(string);
  }

  public static String firstToLowerCase(final String string) {
    if(string.isEmpty()) return "";
    else{
      final int cp0 = string.codePointAt(0);
      if(Character.isLowerCase(cp0)) return string;
      else return collect(IntStream.concat(
        IntStream.of(Character.toLowerCase(cp0)),
        string.codePoints().skip(1)
      ));
    }
  }

  public static String collect(final IntStream codepoints){
    return codepoints
      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
      .toString()
    ;
  }

}
