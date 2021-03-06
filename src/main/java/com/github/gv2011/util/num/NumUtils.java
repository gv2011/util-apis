package com.github.gv2011.util.num;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class NumUtils {

  static final BigInteger MIN_INT = BigInteger.valueOf(Integer.MIN_VALUE);
  static final BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);

  static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
  static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

  private NumUtils(){staticClass();}
  
  public static Decimal parse(String dec){
    return from(new BigDecimal(dec));
  }

  public static Decimal from(Number number){
    return from(BigDecimal.valueOf(number.doubleValue()));
  }

  public static Decimal from(long l){
    return Integer.MIN_VALUE <= l && l <= Integer.MAX_VALUE
      ? from((int)l)
      : from(BigDecimal.valueOf(l))
    ;
  }

  public static Decimal from(BigInteger i){
    return from(new BigDecimal(i));
  }

  public static Decimal from(int i){
    return IntDecimal.from(i);
  }

  public static Decimal from(BigDecimal bigDecimal){
    return fromCanonical(BigDecimalUtils.canonical(bigDecimal));
  }

  static Decimal fromCanonical(BigDecimal canonical){
    assert BigDecimalUtils.isCanonical(canonical);
    return BigDecimalUtils.canonicalFitsInt(canonical)
      ? IntDecimal.from(canonical.intValueExact())
      : new DefaultDecimal(canonical)
    ;
  }

  public static boolean isOdd(final int n){
    return n%2==1;
  }

  public static String withLeadingZeros(final int i, final int digits){
    final StringBuilder sb = new StringBuilder(Integer.toString(Math.abs(i)));
    verify(sb.length()<=digits);
    while(sb.length()<digits) sb.insert(0, '0');
    if(i<0) sb.insert(0,'-');
    return sb.toString();
  }
  
  public static boolean isInt(final BigInteger i) {
    return i.compareTo(MIN_INT)>=0 && i.compareTo(MAX_INT)<=0;
  }

  public static boolean isLong(final BigInteger i) {
    return i.compareTo(MIN_LONG)>=0 && i.compareTo(MAX_LONG)<=0;
  }

  public static Decimal zero() {
    return IntDecimal.ZERO;
  }

}
