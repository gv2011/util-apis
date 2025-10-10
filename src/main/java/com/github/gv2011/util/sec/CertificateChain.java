package com.github.gv2011.util.sec;

import java.security.cert.X509Certificate;

import com.github.gv2011.util.icol.IList;

public interface CertificateChain {

  default X509Certificate leafCertificate(){
    return certificates().get(0);
  }

  IList<X509Certificate> certificates();

}
