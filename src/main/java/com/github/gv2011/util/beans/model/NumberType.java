package com.github.gv2011.util.beans.model;
import com.github.gv2011.util.beans.Final;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.num.Decimal;

@Final
public interface NumberType extends ElementaryType{

  Opt<Decimal> minValue();

  Opt<Decimal> maxValue();

  Boolean isIntgeger();

}
