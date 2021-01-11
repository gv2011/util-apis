package com.github.gv2011.util.http;

import java.util.function.Consumer;

import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.ServerCertificate;

public interface CertificateHandler {
  
  Opt<ServerCertificate> getCertificate(Domain host, Consumer<CertificateUpdate> updater);

}
