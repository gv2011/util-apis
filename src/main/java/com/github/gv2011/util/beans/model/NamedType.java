package com.github.gv2011.util.beans.model;

import com.github.gv2011.util.beans.AbstractIntermediate;

@AbstractIntermediate
public interface NamedType extends Type{

  QName qName();

}
