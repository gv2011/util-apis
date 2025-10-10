package com.github.gv2011.util.sec;

import java.security.cert.X509Certificate;

import com.github.gv2011.util.Equal;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Opt;
import java.security.PublicKey;

final class CertificateChainImp implements CertificateChain {

  private final IList<X509Certificate> certificates;

  CertificateChainImp(final IList<X509Certificate> certificates) {
    Opt<PublicKey> publicKey = Opt.empty();
    for(final X509Certificate cert: certificates.reversed()){
      publicKey.ifPresentDo(pk->cert.verify(pk));
      publicKey = Opt.of(cert.getPublicKey());
    }
    this.certificates = certificates;
  }

  @Override
  public IList<X509Certificate> certificates() {
    return certificates;
  }

  @Override
  public int hashCode() {
    return Equal.hashCode(CertificateChain.class, certificates);
  }

  @Override
  public boolean equals(final Object obj) {
    return Equal.calcEqual(this, obj, CertificateChain.class, CertificateChain::certificates);
  }

  @Override
  public String toString() {
    return certificates.toString();
  }

}
