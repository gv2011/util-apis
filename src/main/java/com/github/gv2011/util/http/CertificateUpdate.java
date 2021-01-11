package com.github.gv2011.util.http;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.sec.ServerCertificate;

public interface CertificateUpdate extends Bean{
  
  ServerCertificate newCertificate();

}
