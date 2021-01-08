package com.github.gv2011.util.http;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.Opt;

public interface Request extends HttpMessage, Bean{
  
  Opt<HostName> host();

  Method method();

  //TODO: replace with Path
  IList<String> path();
  
  ISortedMap<String, IList<String>> parameters();

}
