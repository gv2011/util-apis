package com.github.gv2011.util.http;

import java.security.cert.X509Certificate;
import java.util.function.Consumer;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.ServerCertificate;

public interface CertificateHandler extends AutoCloseableNt{

  ISet<Domain> availableDomains();

  Opt<ServerCertificate> getCertificate(Domain host, Consumer<CertificateUpdate> updater);

  default X509Certificate rootCertificate(){
    throw new UnsupportedOperationException();
  }

}
