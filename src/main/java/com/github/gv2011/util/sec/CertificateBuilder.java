package com.github.gv2011.util.sec;

import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;

import javax.naming.ldap.LdapName;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.icol.ISortedSet;

public interface CertificateBuilder{

  CertificateBuilder setSubject(final LdapName subject);

  CertificateBuilder setDomains(final Pair<Domain, ISortedSet<Domain>> domains);

  CertificateBuilder setSubjectPublicKey(final RSAPublicKey subjectPublicKey);

  CertificateBuilder setNotBefore(final Instant notBefore);

  CertificateBuilder setNotAfter(final Instant notAfter);

  CertificateBuilder setIssuer(final LdapName issuer);

  X509Certificate build(final RsaKeyPair keyPair);

}
