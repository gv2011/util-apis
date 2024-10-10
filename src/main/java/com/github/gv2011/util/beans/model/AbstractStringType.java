package com.github.gv2011.util.beans.model;

import com.github.gv2011.util.beans.AbstractIntermediate;
import com.github.gv2011.util.icol.Opt;

@AbstractIntermediate
public interface AbstractStringType extends ElementaryType{

  default Integer minLength() {
    return 0;
  }

  default Opt<Integer> maxLength() {
    return Opt.empty();
  }

}
