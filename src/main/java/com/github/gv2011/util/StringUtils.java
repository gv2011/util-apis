package com.github.gv2011.util;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import static com.github.gv2011.util.CollectionUtils.toSortedSet;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.icol.ICollections.listBuilder;

import java.util.Arrays;
import java.util.Locale;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IList.Builder;
import com.github.gv2011.util.icol.Opt;

public final class StringUtils {

  private StringUtils(){staticClass();}

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
    if (factor < 0) throw new IllegalArgumentException();
    final char[] chars = new char[factor];
    Arrays.fill(chars, c);
    return new String(chars);
  }

  public static String alignRight(final Object str, final int size) {
    return alignRight(str.toString(), size, ' ');
  }

  public static String alignRight(final CharSequence str, final int size, final char fill) {
    final char[] chars = new char[size];
    final int fillSize = size - str.length();
    if (fillSize < 0) throw new IllegalArgumentException("Does not fit.");
    for (int i = 0; i < fillSize; i++)
      chars[i] = fill;
    for (int i = 0; i < str.length(); i++)
      chars[fillSize + i] = str.charAt(i);
    return String.copyValueOf(chars);
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

}
