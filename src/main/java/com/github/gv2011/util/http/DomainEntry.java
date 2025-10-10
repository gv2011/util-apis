package com.github.gv2011.util.http;

import java.time.Instant;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.sec.CertificateChain;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.RsaKeyPair;

public interface DomainEntry extends Bean{

  Domain domain();

  RsaKeyPair key();

  Opt<CertificateChain> certificateChain();

  Opt<Instant> lastError();

}
