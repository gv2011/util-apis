package com.github.gv2011.util.beans.model;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.beans.Computed;

public interface QName extends ModelElement, Bean{

  String namespace();

  String name();

  @Computed
  String qualifiedName();

}
