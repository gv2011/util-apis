package com.github.gv2011.util.beans.model;

import com.github.gv2011.util.beans.Final;

@Final
public interface PairType extends Type{

  TypeSpec leftType();

  TypeSpec rightType();

}
