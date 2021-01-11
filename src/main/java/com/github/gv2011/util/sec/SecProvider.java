package com.github.gv2011.util.sec;

import com.github.gv2011.util.bytes.TypedBytes;

public interface SecProvider {
  
  static SecProvider instance(){return SecUtils.SEC_PROVIDER.get();}

  
  CertificateBuilder createCertificateBuilder();
  
  SimpleKeyStore createSimpleKeyStore(Domain domain);

  SimpleKeyStore loadSimpleKeyStore(TypedBytes bytes);

}
