package com.github.gv2011.util.http;

import java.security.PublicKey;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.sec.CertificateChain;
import com.github.gv2011.util.sec.Domain;

public interface Request extends HttpMessage, Bean{

  Domain host();

  Boolean secure();

  Opt<CertificateChain> peerCertificateChain();

  default Opt<PublicKey> peerPublicKey(){
    return peerCertificateChain().map(cc->cc.leafCertificate().getPublicKey());
  }

  Method method();

  //TODO: replace with Path
  IList<String> path();

  ISortedMap<String, IList<String>> parameters();

  ISortedMap<String,IList<String>> headers();

}
