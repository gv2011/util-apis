package com.github.gv2011.util.beans.model;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.icol.Opt;

public interface Cardinality extends ModelElement, Bean{

  default Integer minOccurs() {
    return 0;
  }

  default Opt<Integer> maxOccurs() {
    return Opt.empty();
  }

}
