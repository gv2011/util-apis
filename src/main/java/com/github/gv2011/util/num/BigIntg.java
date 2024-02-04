package com.github.gv2011.util.num;

import static com.github.gv2011.util.num.BigDecimalUtils.MIN_INT;
import static com.github.gv2011.util.num.BigDecimalUtils.isCanonical;
import static com.github.gv2011.util.num.BigDecimalUtils.isInIntRange;

import java.math.BigDecimal;
import java.util.stream.Stream;

final class BigIntg extends AbstractIntg implements Intg{

  private final BigDecimal dec;

  public static Decimal from(final BigDecimal canonical) {
    return new BigIntg(canonical);
  }

  static final Intg INTG_MIN_INT = new BigIntg(MIN_INT);

  private BigIntg(final BigDecimal canonical) {
    assert
      isCanonical(canonical) &&
      BigDecimalUtils.isInteger(canonical) &&
      (!isInIntRange(canonical) || canonical.equals(MIN_INT))
    ;
    this.dec = canonical;
  }

  @Override
  public final BigDecimal toBigDecimal() {
    return dec;
  }

  @Override
  public boolean equals(final Object obj) {
    return this==obj ? true : obj==null ? false : obj instanceof Decimal
      ? compareWithOtherOfSameType((Decimal)obj)==0
      : false
    ;
  }

  @Override
  public final boolean isZero() {
    return false;
  }

  @Override
  public Stream<Intg> range(final Intg exclusive) {
    if(gte(exclusive)) return Stream.empty();
    else{
      final Intg last = exclusive.minusOne();
      return Stream.iterate(this, i->i.lt(last), i->i.plus(one()));
    }
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
