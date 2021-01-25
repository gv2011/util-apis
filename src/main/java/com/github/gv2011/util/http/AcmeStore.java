package com.github.gv2011.util.http;

import java.net.URI;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.RsaKeyPair;
import com.github.gv2011.util.sec.ServerCertificate;

public interface AcmeStore extends AutoCloseableNt{
  
  boolean production();
  
  RsaKeyPair userKeyPair();
  
  URI acmeUrl();
  
  Opt<URI> accountUrl();
  
  ISet<Domain> availableDomains();

  DomainEntry getEntry(Domain host);

  void setAccountUrl(URI uri);

  void add(ServerCertificate serverCertificate);

  void setError(Domain domain);


}
