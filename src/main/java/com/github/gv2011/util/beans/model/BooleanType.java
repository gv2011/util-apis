package com.github.gv2011.util.beans.model;

import com.github.gv2011.util.beans.Final;
import com.github.gv2011.util.beans.FixedValue;
import com.github.gv2011.util.icol.ISortedSet;

@Final
public interface BooleanType extends AbstractEnumType{

  @Override
  @FixedValue({"false", "true"})
  ISortedSet<String> constants();

}
