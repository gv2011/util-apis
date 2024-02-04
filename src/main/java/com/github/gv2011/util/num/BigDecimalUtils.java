package com.github.gv2011.util.num;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.github.gv2011.util.StringUtils;

public final class BigDecimalUtils {

  public static final BigDecimal ZERO = canonical(BigDecimal.ZERO);

  public static final BigDecimal MIN_INT = canonical(BigDecimal.valueOf(Integer.MIN_VALUE));
  public static final BigDecimal MAX_INT = canonical(BigDecimal.valueOf(Integer.MAX_VALUE));

  public static final BigDecimal MIN_LONG = canonical(BigDecimal.valueOf(Long.MIN_VALUE));
  public static final BigDecimal MAX_LONG = canonical(BigDecimal.valueOf(Long.MAX_VALUE));

  private BigDecimalUtils(){staticClass();}

  public static int digits(final BigDecimal dec){
    return dec.stripTrailingZeros().precision();
  }

  public static BigInteger mantissa(final BigDecimal dec){
    return dec.stripTrailingZeros().unscaledValue();
  }

  public static int exponent(final BigDecimal dec){
    return -dec.stripTrailingZeros().scale();
  }

  public static boolean isInteger(final BigDecimal dec){
    return canonicalIsInteger(canonical(dec));
  }

  static boolean canonicalIsInteger(final BigDecimal canonical){
    assert(isCanonical(canonical));
    return canonical.scale()<=0;
  }

  public static BigDecimal canonical(final BigDecimal dec){
    return dec.stripTrailingZeros();
  }

  public static boolean isCanonical(final BigDecimal dec){
    return canonical(dec).equals(dec);
  }

  public static final String toEcmaString(final BigDecimal bigDecimal) {
    assert BigDecimalUtils.isCanonical(bigDecimal);
    final String result;
    final int signum = bigDecimal.signum();
    if(signum==0) result = "0";
    else{
      final BigDecimal dec = bigDecimal.abs();
      final String sign = signum==-1 ? "-" : "";
      final int k = dec.precision();
      final int e = - dec.scale();
      final int n = e+k;
      final String mantissa = dec.unscaledValue().toString();
      if( k<=n  &&  n <= 21 ){
        result = sign + mantissa + StringUtils.multiply('0', e);
      }
      else if( 0 < n  &&  n <= 21 ){
        result = sign + mantissa.substring(0, n) + '.' + mantissa.substring(n);
      }
      else if( -6 < n  &&  n <= 0 ){
        result = sign + "0." + StringUtils.multiply('0', -n) + mantissa;
      }
      else if(k==1){
        result = sign + mantissa + expStr(n-1);
      }
      else{
        result = sign + mantissa.charAt(0) + "." + mantissa.substring(1) + expStr(n-1);
      }
    }
    assert new BigDecimal(result).compareTo(bigDecimal)==0;
    return result;
  }

  private static final String expStr(final int e){
    return 'e' + (e>=0?"+":"") + e;
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

  public static boolean fitsInt(final BigDecimal dec){
    return canonicalFitsInt(canonical(dec));
  }

  public static boolean fitsLong(final BigDecimal dec){
    return canonicalFitsLong(canonical(dec));
  }

  public static boolean canonicalFitsInt(final BigDecimal canonical){
    assert(isCanonical(canonical));
    return canonicalIsInteger(canonical) && isInIntRange(canonical);
  }

  public static boolean isInIntRange(final BigDecimal dec){
    return
      dec.compareTo(MIN_INT) >= 0 &&
      dec.compareTo(MAX_INT) <= 0
    ;
  }

  public static boolean canonicalFitsLong(final BigDecimal canonical){
    assert(isCanonical(canonical));
    return canonicalIsInteger(canonical) && isInLongRange(canonical);
  }

  public static boolean isInLongRange(final BigDecimal dec){
    return
      dec.compareTo(MIN_LONG) >= 0 &&
      dec.compareTo(MAX_LONG) <= 0
    ;
  }

}
