package com.github.gv2011.util.beans.model;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.beans.Computed;

public interface Property extends ModelElement, Bean{

  QName name();

  TypeSpec type();

  Cardinality cardinality();

  Structure structure();

  @Computed
  Boolean isMap();

  @Computed
  Boolean isElementaryMap();

}
