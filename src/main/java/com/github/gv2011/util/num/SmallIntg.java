package com.github.gv2011.util.num;

import static com.github.gv2011.util.CollectionUtils.pair;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.gv2011.util.Pair;

final class SmallIntg extends AbstractIntg implements Intg {

  private static final int MIN_TABLE = -32;
  private static final int SIZE = 256;
  private static final SmallIntg[] TABLE;

  static final SmallIntg MINUS_ONE;
  static final SmallIntg ZERO;
  static final SmallIntg ONE;
  static final SmallIntg TWO;
  static final SmallIntg THREE;
  static final SmallIntg TEN;

  static final SmallIntg MIN;
  static final SmallIntg MAX;

  static{
    TABLE = new SmallIntg[SIZE];
    for(int i = 0; i<TABLE.length; i++) TABLE[i] = new SmallIntg(MIN_TABLE+i);
    MINUS_ONE = TABLE[-1-MIN_TABLE];
    ZERO =  TABLE[-MIN_TABLE];
    ONE =   TABLE[1-MIN_TABLE];
    TWO =   TABLE[2-MIN_TABLE];
    THREE = TABLE[3-MIN_TABLE];
    TEN =   TABLE[10-MIN_TABLE];
    MAX = new SmallIntg(Integer.MAX_VALUE);
    MIN = MAX.negate();
    assert MIN.intValue()-1 == Integer.MIN_VALUE;
  }

  private final int i;

  static SmallIntg intg(final int i) {
    return MIN_TABLE<=i && i<MIN_TABLE+SIZE ? TABLE[i-MIN_TABLE] : new SmallIntg(i);
  }

  private SmallIntg(final int i) {
    assert i!=Integer.MIN_VALUE;
    this.i = i;
  }

  @Override
  public BigDecimal toBigDecimal() {
    return BigDecimalUtils.canonical(BigDecimal.valueOf(i));
  }

  @Override
  int compareWithOtherAbstractDecimal(final AbstractDecimal o) {
    if(o instanceof SmallIntg) return Integer.compare(toInt(), o.toInt());
    else return super.compareWithOtherAbstractDecimal(o);
  }

  @Override
  public boolean isZero() {
    return i==0;
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
  public boolean equals(final Object obj) {
    return this==obj ? true
      : obj==null                ? false
      : obj instanceof SmallIntg ? toInt()==((SmallIntg)obj).toInt()
      : obj instanceof Decimal   ? compareWithOtherOfSameType((Decimal)obj)==0
      : false
    ;
  }

  @Override
  public String toEcmaString() {
    final String result = Integer.toString(i);
    assert result.equals(super.toEcmaString());
    return result;
  }

  @Override
  public SmallIntg negate() {
    // i != Integer.MIN_VALUE
    return intg(-i);
  }

  @Override
  public int signum() {
    return Integer.signum(i);
  }

  @Override
  public boolean gt(final Decimal other) {
    return other.fitsInt() ? i>other.intValue() : super.gt(other);
  }

  @Override
  public boolean gte(final Decimal other) {
    return other.fitsInt() ? i>=other.intValue() : super.gte(other);
  }

  @Override
  public boolean lt(final Decimal other) {
    return other.fitsInt() ? i<other.intValue() : super.lt(other);
  }

  @Override
  public boolean lte(final Decimal other) {
    return other.fitsInt() ? i<=other.intValue() : super.lte(other);
  }

  @Override
  public Decimal plus(final Decimal dec) {
    return dec.fitsInt()
      ? NumUtils.num((long)i + (long)dec.toInt())
      : super.plus(dec)
    ;
  }

  @Override
  public Intg plus(final Intg summand) {
    return summand.fitsInt()
      ? NumUtils.intg((long)i + (long)summand.toInt())
      : super.plus(summand)
    ;
  }

  @Override
  public Decimal multiply(final Decimal factor) {
    return factor.fitsInt()
      ? NumUtils.num((long)i * (long)factor.toInt())
      : super.multiply(factor)
    ;
  }

  @Override
  public Pair<Intg, Intg> divR(final Decimal divisor) {
    if(divisor.fitsInt()){
      final int i = toInt();
      final int div = divisor.intValue();
      final int q = i / div;
      return pair(intg(q), intg(i-(div * q)));
    }
    else return super.divR(divisor);
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

  @Override
  public Stream<Intg> range(final Intg exclusive) {
    return exclusive.fitsInt()
      ? IntStream.range(i, exclusive.intValue()).mapToObj(SmallIntg::intg)
      : exclusive.isNegative()
      ? Stream.empty()
      : Stream.concat(
        IntStream.rangeClosed(i, MAX.intValue()).<Intg>mapToObj(SmallIntg::intg),
        MAX.plus(one()).range(exclusive)
      )
    ;
  }

  @Override
  public boolean isOne() {
    return i==1;
  }

  @Override
  public boolean isTwo() {
    return i==2;
  }

}
