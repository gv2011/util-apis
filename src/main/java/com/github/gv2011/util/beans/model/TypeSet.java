package com.github.gv2011.util.beans.model;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;

public interface TypeSet extends ModelElement, Bean{

  Opt<TypeRef> root();

  ISet<Type> types();

  Type resolve(TypeSpec t);

}
