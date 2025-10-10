package com.github.gv2011.util.sec;

import static com.github.gv2011.util.ex.Exceptions.format;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.beans.Final;
import com.github.gv2011.util.beans.Validator;

@Final(validator=ServerCertificate.V.class)
public interface ServerCertificate extends Bean{

  Domain domain();

  RsaKeyPair keyPair();

  CertificateChain certificateChain();


  public static final class V implements Validator<ServerCertificate>{
    @Override
    public String invalidMessage(final ServerCertificate cert) {
      return isValid(cert) ? "" :
        format(
          "The provided key and certificate chain do not match.\nChain key: {}\nPublic key: {}.",
          cert.certificateChain().leafCertificate().getPublicKey(), cert.keyPair().getPublic()
        )
      ;
    }
    @Override
    public boolean isValid(final ServerCertificate cert) {
      return cert.certificateChain().leafCertificate().getPublicKey().equals(cert.keyPair().getPublic());
    }
  }

}
