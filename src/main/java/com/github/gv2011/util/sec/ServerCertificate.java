package com.github.gv2011.util.sec;

import java.security.cert.X509Certificate;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.icol.IList;

public interface ServerCertificate extends Bean{
  
  Domain domain();
  
  RsaKeyPair keyPair();
  
  IList<X509Certificate> certificateChain();

}
