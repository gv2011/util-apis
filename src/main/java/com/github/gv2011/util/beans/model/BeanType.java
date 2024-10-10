package com.github.gv2011.util.beans.model;

import com.github.gv2011.util.beans.Final;
import com.github.gv2011.util.icol.ISet;

@Final
public interface BeanType extends NamedType{

  ISet<Property> properties();

  ISet<TypeSpec> subTypes();

}
