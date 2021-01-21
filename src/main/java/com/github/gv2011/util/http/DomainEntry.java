package com.github.gv2011.util.http;

import java.security.cert.X509Certificate;
import java.time.Instant;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.RsaKeyPair;

public interface DomainEntry extends Bean{
  
  Domain domain();
  
  RsaKeyPair key();

  IList<X509Certificate> certificateChain();
  
  Opt<Instant> lastError();

}
