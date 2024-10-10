package com.github.gv2011.util.beans.model;

import com.github.gv2011.util.beans.AbstractRoot;
import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.beans.TypeNameProperty;

@AbstractRoot(subClasses={BeanType.class, PairType.class, AbstractBinaryType.class, AbstractEnumType.class, AbstractStringType.class, NumberType.class, BooleanType.class, TypeRef.class})
public interface TypeSpec extends Bean{

  @TypeNameProperty
  String kind();

}
