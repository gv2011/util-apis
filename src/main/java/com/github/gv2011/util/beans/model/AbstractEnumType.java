package com.github.gv2011.util.beans.model;

import com.github.gv2011.util.beans.AbstractIntermediate;
import com.github.gv2011.util.icol.ISortedSet;

@AbstractIntermediate
public interface AbstractEnumType extends AbstractStringType{

  ISortedSet<String> constants();

}
