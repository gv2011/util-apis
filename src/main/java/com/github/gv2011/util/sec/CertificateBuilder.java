package com.github.gv2011.util.sec;

import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;

import javax.naming.ldap.LdapName;
import javax.security.auth.x500.X500Principal;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.icol.ISortedSet;

public interface CertificateBuilder{

  CertificateBuilder setCa(final boolean caCertificate);

  CertificateBuilder setSubject(final LdapName subject);

  CertificateBuilder setDomains(final Pair<Domain, ISortedSet<Domain>> domains);

  CertificateBuilder setSubjectPublicKey(final RSAPublicKey subjectPublicKey);

  CertificateBuilder setNotBefore(final Instant notBefore);

  CertificateBuilder setNotAfter(final Instant notAfter);

  CertificateBuilder setIssuer(final LdapName issuer);

  CertificateBuilder setIssuer(X500Principal x500Principal);

  X509Certificate build(final RsaKeyPair keyPair);

}
