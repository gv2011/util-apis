package com.github.gv2011.util.num;

import static com.github.gv2011.util.num.NumUtils.intg;

abstract class AbstractIntg extends AbstractDecimal implements Intg{

  @Override
  public final Intg self() {
    return this;
  }

  @Override
  public final Intg toIntg() {
    return this;
  }

  @Override
  public final boolean isIntg() {
    return true;
  }

  @Override
  public boolean fitsInt() {
    return BigDecimalUtils.isInIntRange(toBigDecimal());
  }

  @Override
  public boolean fitsLong() {
    return BigDecimalUtils.isInLongRange(toBigDecimal());
  }

  @Override
  public Intg plus(final Intg augend){
    return intg(toBigDecimal().add(augend.toBigDecimal()));
  }

  @Override
  public Intg negate(){
    return intg(toBigDecimal().negate());
  }

  @Override
  public final Intg minus(final Intg i){
    return plus(i.negate());
  }

  @Override
  public Intg multiply(final Intg multiplicand){
    return intg(toBigDecimal().multiply(multiplicand.toBigDecimal()));
  }
}
