package com.github.gv2011.util.num;

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.icol.ICollections.filledList;
import static com.github.gv2011.util.icol.ICollections.listBuilder;
import static com.github.gv2011.util.num.NumUtils.doNTimes;
import static com.github.gv2011.util.num.NumUtils.intg;
import static com.github.gv2011.util.num.NumUtils.num;
import static com.github.gv2011.util.num.SmallIntg.MINUS_ONE;

import java.math.BigDecimal;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IList.Builder;
import com.github.gv2011.util.tstr.TypedString;

abstract class AbstractDecimal implements Decimal{

  @Override
  public final int compareWithOtherOfSameType(final Decimal o) {
    return compareWithOtherAbstractDecimal(
      AbstractDecimal.class.cast(
        AbstractDecimal.class.isInstance(o)
        ? o
        : NumUtils.parse(o.toString())
      )
    );
  }

  @Override
  public final int hashCode() {
    return TypedString.hashCode(this);
  }

  @Override
  public abstract boolean equals(Object obj);

  @Override
  public final Class<Decimal> clazz() {
    return Decimal.class;
  }

  int compareWithOtherAbstractDecimal(final AbstractDecimal o) {
    return toBigDecimal().compareTo(o.toBigDecimal());
  }

  @Override
  public boolean fitsInt() {
    return BigDecimalUtils.canonicalFitsInt(toBigDecimal());
  }

  @Override
  public int intValue() {
    return toBigDecimal().intValueExact();
  }

  @Override
  public boolean fitsLong() {
    return BigDecimalUtils.canonicalFitsLong(toBigDecimal());
  }

  @Override
  public long longValue() {
    return toBigDecimal().longValueExact();
  }

  @Override
  public double doubleValue() {
    return toBigDecimal().doubleValue();
  }

  @Override
  public final String toString() {
    return toEcmaString();
  }

  @Override
  public int signum() {
    return toBigDecimal().signum();
  }

  @Override
  public final Intg minusOne() {
    return SmallIntg.MINUS_ONE;
  }

  @Override
  public final Intg zero() {
    return SmallIntg.ZERO;
  }

  @Override
  public final Intg one() {
    return SmallIntg.ONE;
  }

  @Override
  public final Intg two() {
    return SmallIntg.ONE;
  }

  @Override
  public final Intg ten() {
    return SmallIntg.TEN;
  }

  @Override
  public boolean gt(final Decimal other) {
    return toBigDecimal().compareTo(other.toBigDecimal())>0;
  }

  @Override
  public boolean gte(final Decimal other) {
    return toBigDecimal().compareTo(other.toBigDecimal())>=0;
  }

  @Override
  public boolean lt(final Decimal other) {
    return toBigDecimal().compareTo(other.toBigDecimal())<0;
  }

  @Override
  public boolean lte(final Decimal other) {
    return toBigDecimal().compareTo(other.toBigDecimal())<=0;
  }

  @Override
  public Decimal plus(final Decimal dec) {
     return num(toBigDecimal().add(dec.toBigDecimal()));
  }

  @Override
  public Decimal negate() {
    return num(toBigDecimal().negate());
  }

  @Override
  public final Decimal minus(final Decimal dec) {
    return plus(dec.negate());
  }

  @Override
  public Decimal multiply(final Decimal factor) {
    return factor.equals(one())
      ? this
      : num(toBigDecimal().multiply(factor.toBigDecimal()))
    ;
  }

  @Override
  public Pair<Intg, Intg> divR(final Decimal divisor) {
    final BigDecimal[] divR = toBigDecimal().divideAndRemainder(divisor.toBigDecimal());
    return pair(intg(divR[0]), intg(divR[1]));
  }

  @Override
  public final IList<Intg> toBaseIntg(final Intg base) {
    return toBaseIntg(base, MINUS_ONE);
  }

  @Override
  public final Pair<IList<Intg>,Intg> toBase(final Intg base) {
    return toBase(base, true, MINUS_ONE);
  }

  @Override
  public IList<Intg> toBaseIntg(final Intg base, final Intg digits) {
    verify(isIntg());
    final Pair<IList<Intg>, Intg> withExp = toBase(base, false, digits);
    assert withExp.getValue().isZero();
    return withExp.getKey();
  }

  private final Pair<IList<Intg>,Intg> toBase(final Decimal base, final boolean noLeadingZeroes, final Intg digits) {
    verify(
      base,
      b->(
        b.gte(two()) &&
        b.isIntg() &&
        (isIntg() || b.isMultipleOf(ten()))
      )
    );
    final Pair<IList<Intg>,Intg> result;
    if(isZero()){
      result = pair(
        filledList(zero(), digits.equals(MINUS_ONE) ? 1 : digits.intValue()),
        zero()
      );
    }
    else{
      int exp = 0;
      Decimal dec = abs();
      while(!dec.isIntg()){
        dec = dec.multiply(base);
        exp--;
      }
      final Builder<Intg> lb = listBuilder();
      boolean trailingZeroes = noLeadingZeroes;
      while(!dec.isZero()){
        final Pair<Intg, Intg> divR = dec.divR(base);
        final Intg remainder = divR.getValue();
        assert remainder.signum() >= 0 && remainder.isIntg() && remainder.lt(base);
        if(trailingZeroes){
          if(remainder.isZero()) exp++;
          else trailingZeroes = false;
        }
        if(!trailingZeroes)lb.insert(0, remainder);
        dec = divR.getKey();
      }
      if(!digits.equals(MINUS_ONE)){
        final int leadingZeroes = digits.intValue()-lb.size();
        verify(leadingZeroes>=0);
        doNTimes(leadingZeroes, ()->lb.insert(0, zero()));
      }
      result = pair(lb.build(), intg(exp));
    }
    return result;
  }

  @Override
  public String toEcmaString() {
    return BigDecimalUtils.toEcmaString(toBigDecimal());
  }

}
