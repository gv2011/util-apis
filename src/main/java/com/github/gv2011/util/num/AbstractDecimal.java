package com.github.gv2011.util.num;

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



  abstract int compareWithOtherAbstractDecimal(final AbstractDecimal o);

}
