package com.github.gv2011.util.num;

import static com.github.gv2011.util.num.NumUtils.intg;

import java.util.stream.Stream;

public interface Intg extends Decimal{

  @Override
  Intg negate();

  @Override
  Intg self();

  @Override
  default Intg abs(){
    return signum()==-1 ? negate() : this;
  }

  @Override
  default Intg plus(final Intg augend){
    return intg(toBigDecimal().add(augend.toBigDecimal()));
  }

  default Intg plus(final long augend){
    return plus(intg(augend));
  }

  @Override
  default Intg plusOne(){
    return plus(one());
  }

  @Override
  Intg minus(final Intg i);

  @Override
  Intg multiply(final Intg factor);

  default Stream<Intg> stream(){
    return zero().range(this);
  }

  Stream<Intg> range(Intg exlusive);

  default Stream<Intg> unlimitedStream(){
    return Stream.iterate(this, Intg::plusOne);
  }

  @Override
  default Intg min(final Intg other) {
    return lte(other) ? this : other;
  }

  String toString(int minDigits);
}
