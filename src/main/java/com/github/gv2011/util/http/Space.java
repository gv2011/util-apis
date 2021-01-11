package com.github.gv2011.util.http;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.sec.Domain;

public interface Space extends Bean{
  
  Opt<Domain> host();
  
  IList<String> path();

}
