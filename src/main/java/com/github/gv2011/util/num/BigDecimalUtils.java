package com.github.gv2011.util.num;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class BigDecimalUtils {

  public static final BigDecimal ZERO = canonical(BigDecimal.ZERO);
  
  public static final BigDecimal MIN_INT = canonical(BigDecimal.valueOf(Integer.MIN_VALUE));
  public static final BigDecimal MAX_INT = canonical(BigDecimal.valueOf(Integer.MAX_VALUE));
  
  public static final BigDecimal MIN_LONG = canonical(BigDecimal.valueOf(Long.MIN_VALUE));
  public static final BigDecimal MAX_LONG = canonical(BigDecimal.valueOf(Long.MAX_VALUE));
  
  private BigDecimalUtils(){staticClass();}
  
  public static int digits(BigDecimal dec){
    return dec.stripTrailingZeros().precision();
  }
  
  public static BigInteger mantissa(BigDecimal dec){
    return dec.stripTrailingZeros().unscaledValue();
  }
  
  public static int exponent(BigDecimal dec){
    return -dec.stripTrailingZeros().scale();
  }
  
  public static boolean isInteger(BigDecimal dec){
    return canonicalIsInteger(canonical(dec));
  }
  
  static boolean canonicalIsInteger(BigDecimal canonical){
    assert(isCanonical(canonical));
    return canonical.scale()<=0;
  }
  
  public static BigDecimal canonical(final BigDecimal dec){
    return dec.stripTrailingZeros();
  }

  public static boolean isCanonical(final BigDecimal dec){
    return canonical(dec).equals(dec);
  }
  
  public static String toEcmaString(BigDecimal dec) {
    return DefaultDecimal.toEcmaString(canonical(dec));
  }

  public static BigDecimal toEngineering(final BigDecimal dec){
    final BigDecimal result;
    final BigDecimal stripped = dec.stripTrailingZeros();
    final int scale = stripped.scale();
    final int scaleRem = (((-scale) % 3)+3)%3;
    result = stripped.setScale(scale+scaleRem);
    assert result.compareTo(dec)==0;
    return result;
  }

  public static boolean fitsInt(BigDecimal dec){
    return canonicalFitsInt(canonical(dec));
  }
  
  public static boolean canonicalFitsInt(BigDecimal canonical){
    assert(isCanonical(canonical));
    return canonicalIsInteger(canonical)
      && canonical.compareTo(MIN_INT) >= 0
      && canonical.compareTo(MAX_INT) <= 0
    ;
  }
  

}
