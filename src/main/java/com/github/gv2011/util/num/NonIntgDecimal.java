package com.github.gv2011.util.num;

import static com.github.gv2011.util.ex.Exceptions.format;

import java.math.BigDecimal;

final class NonIntgDecimal extends AbstractDecimal {

  private final BigDecimal dec;

  NonIntgDecimal(final BigDecimal dec) {
    this.dec = BigDecimalUtils.canonical(dec);
    assert
      dec.signum()!=0 &&
      !BigDecimalUtils.canonicalIsInteger(dec);
    ;
  }

  @Override
  public Decimal self() {
    return this;
  }

  @Override
  public BigDecimal toBigDecimal() {
    return dec;
  }

  @Override
  public int toInt() {
    throw new ArithmeticException(format("{} is not an integer.", this));
  }

  @Override
  public boolean isZero() {
    return false;
  }

  @Override
  public boolean fitsInt() {
    return false;
  }

  @Override
  public boolean equals(final Object obj) {
    return this==obj ? true : obj==null ? false : obj instanceof Decimal
      ? compareWithOtherOfSameType((Decimal)obj)==0
      : false
    ;
  }

  @Override
  public boolean isIntg() {
    return false;
  }

  @Override
  public boolean fitsLong() {
    return BigDecimalUtils.MIN_LONG.compareTo(dec)<=0 && dec.compareTo(BigDecimalUtils.MAX_LONG)<=0;
  }

  @Override
  public Intg toIntg() {
    throw new ArithmeticException(format("{} is not an integer.", this));
  }

  @Override
  public boolean isOne() {
    return false;
  }

  @Override
  public boolean isTwo() {
    return false;
  }

}
