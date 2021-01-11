package com.github.gv2011.util.http;

import java.security.cert.X509Certificate;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.sec.Domain;

public interface Request extends HttpMessage, Bean{
  
  Domain host();
  
  Boolean secure();
  
  IList<X509Certificate> peerCertificateChain();

  Method method();

  //TODO: replace with Path
  IList<String> path();
  
  ISortedMap<String, IList<String>> parameters();

}
