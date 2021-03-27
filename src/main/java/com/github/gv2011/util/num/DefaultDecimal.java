package com.github.gv2011.util.num;

import java.math.BigDecimal;

import com.github.gv2011.util.StringUtils;

final class DefaultDecimal implements Decimal {
  
  private final BigDecimal dec;

  DefaultDecimal(BigDecimal dec) {
    this.dec = BigDecimalUtils.canonical(dec);
    assert !BigDecimalUtils.canonicalFitsInt(this.dec);
  }

  @Override
  public Decimal self() {
    return this;
  }

  @Override
  public Class<Decimal> clazz() {
    return Decimal.class;
  }

  @Override
  public BigDecimal toBigDecimal() {
    return dec;
  }

  @Override
  public int toInt() {
    throw new ArithmeticException();
  }

  @Override
  public boolean fitsInt() {
    return false;
  }

  @Override
  public int compareWithOtherOfSameType(Decimal o) {
    return dec.compareTo(o.toBigDecimal());
  }

  @Override
  public int hashCode() {
    return dec.hashCode();
  }

  @Override
  public String toString() {
    return toEcmaString();
  }

  @Override
  public boolean equals(Object obj) {
    return this==obj ? true : obj==null ? false : obj instanceof Decimal
      ? compareWithOtherOfSameType((Decimal)obj)==0
      : false
    ;
  }

  @Override
  public String toEcmaString() {
    return toEcmaString(dec);
  }

  static String toEcmaString(BigDecimal bigDecimal) {
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
  
  private static final String expStr(int e){
    return 'e' + (e>=0?"+":"") + e;
  }

  @Override
  public Decimal zero() {
    return IntDecimal.ZERO;
  }

  @Override
  public Decimal one() {
    return IntDecimal.ONE;
  }

  @Override
  public Decimal negate() {
    return NumUtils.from(dec.negate());
  }

  @Override
  public int signum() {
    return dec.signum();
  }

  @Override
  public Decimal plus(Decimal dec) {
    return sum(this, dec);
  }

  static Decimal sum(Decimal d1, Decimal d2) {
    return NumUtils.from(d1.toBigDecimal().add(d2.toBigDecimal()));
  }

  @Override
  public boolean isInteger() {
    return BigDecimalUtils.canonicalIsInteger(dec);
  }

  @Override
  public boolean fitsLong() {
    return BigDecimalUtils.MIN_LONG.compareTo(dec)<=0 && dec.compareTo(BigDecimalUtils.MAX_LONG)<=0;
  }

  /**
   * Always throws an ArithmeticException.
   */
  @Override
  public int intValue() {
    return dec.intValueExact();
  }

  @Override
  public long longValue() {
    return dec.longValueExact();
  }
  
  @Override
  public double doubleValue() {
    return dec.doubleValue();
  }

}
