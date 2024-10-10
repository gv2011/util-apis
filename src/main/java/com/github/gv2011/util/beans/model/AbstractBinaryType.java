package com.github.gv2011.util.beans.model;

import com.github.gv2011.util.beans.AbstractIntermediate;
import com.github.gv2011.util.icol.Opt;

@AbstractIntermediate
public interface AbstractBinaryType extends ElementaryType{

  default Integer minByteLength() {
    return 0;
  }

  default Opt<Integer> maxByteLength() {
    return Opt.empty();
  }

}
