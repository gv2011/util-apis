package com.github.gv2011.util.num;

import java.math.BigDecimal;

final class IntDecimal extends AbstractDecimal {

  private static final int MIN = -32;
  private static final int SIZE = 256;
  private static final IntDecimal[] TABLE;
  static final IntDecimal ZERO;
  static final IntDecimal ONE;

  static{
    TABLE = new IntDecimal[SIZE];
    for(int i = 0; i<TABLE.length; i++) TABLE[i] = new IntDecimal(MIN+i);
    ZERO = TABLE[-MIN];
    ONE = TABLE[1-MIN];
  }

  private final int i;

  static Decimal from(final int i) {
    return MIN<=i && i<MIN+SIZE ? TABLE[i-MIN] : new IntDecimal(i);
  }

  private IntDecimal(final int i) {
    this.i = i;
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
    return BigDecimalUtils.canonical(BigDecimal.valueOf(i));
  }

  @Override
  int compareWithOtherAbstractDecimal(final AbstractDecimal o) {
    if(o instanceof IntDecimal) return toInt()-o.toInt();
    else return toBigDecimal().compareTo(o.toBigDecimal());
  }

  @Override
  public boolean isInteger() {
    return true;
  }

  @Override
  public boolean fitsInt() {
    return true;
  }

  @Override
  public boolean fitsLong() {
    return true;
  }

  @Override
  public int toInt() {
    return i;
  }

  @Override
  public String toString() {
    return toEcmaString();
  }

  @Override
  public boolean equals(final Object obj) {
    return this==obj ? true
      : obj==null                 ? false
      : obj instanceof IntDecimal ? toInt()==((IntDecimal)obj).toInt()
      : obj instanceof Decimal    ? compareWithOtherOfSameType((Decimal)obj)==0
      : false
    ;
  }

  @Override
  public String toEcmaString() {
    final String result = Integer.toString(i);
    assert result.equals(BigDecimalUtils.toEcmaString(toBigDecimal()));
    return result;
  }

  @Override
  public Decimal zero() {
    return ZERO;
  }

  @Override
  public Decimal one() {
    return ONE;
  }

  @Override
  public Decimal negate() {
    return toInt()!=Integer.MIN_VALUE ? from(-toInt()) : NumUtils.from(-((long)toInt()));
  }

  @Override
  public int signum() {
    return Integer.signum(i);
  }

  @Override
  public Decimal plus(final Decimal dec) {
    if(dec.fitsInt()){
      final long sum = i + dec.toInt();
      return Integer.MIN_VALUE <= sum && sum <= Integer.MAX_VALUE
        ? from((int) sum)
        : NumUtils.from(sum)
      ;
    }
    else return DefaultDecimal.sum(this, dec);
  }

  @Override
  public int intValue() {
    return i;
  }

  @Override
  public long longValue() {
    return i;
  }

  @Override
  public double doubleValue() {
    return i;
  }


}
