package com.github.gv2011.util;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * use {@link com.github.gv2011.util.num.NumUtils}
 */
@Deprecated
public final class NumUtils {

  private static final BigInteger MIN_INT = BigInteger.valueOf(Integer.MIN_VALUE);
  private static final BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);

  private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
  private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

  private NumUtils(){staticClass();}

  @Deprecated
  public static boolean isOdd(final int n){
    return n%2==1;
  }

  @Deprecated
  public static String withLeadingZeros(final int i, final int digits){
    final StringBuilder sb = new StringBuilder(Integer.toString(Math.abs(i)));
    verify(sb.length()<=digits);
    while(sb.length()<digits) sb.insert(0, '0');
    if(i<0) sb.insert(0,'-');
    return sb.toString();
  }
  
  @Deprecated
  public static boolean isInt(final BigInteger i) {
    return i.compareTo(MIN_INT)>=0 && i.compareTo(MAX_INT)<=0;
  }

  @Deprecated
  public static boolean isLong(final BigInteger i) {
    return i.compareTo(MIN_LONG)>=0 && i.compareTo(MAX_LONG)<=0;
  }

  @Deprecated
  public static BigDecimal canonical(final BigDecimal dec){
    return dec.stripTrailingZeros();
  }
}
