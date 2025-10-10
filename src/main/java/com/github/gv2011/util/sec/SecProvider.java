package com.github.gv2011.util.sec;

import static com.github.gv2011.util.ex.Exceptions.call;

import java.security.cert.X509Certificate;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.bytes.TypedBytes;

public interface SecProvider {

  static SecProvider instance(){return SecUtils.SEC_PROVIDER.get();}


  CertificateBuilder createCertificateBuilder();

  SimpleKeyStore createSimpleKeyStore(Domain domain);

  SimpleKeyStore loadSimpleKeyStore(TypedBytes bytes);


  default OpenSshPublicKey parseOpenSshRsaPublicKey(final String idRsaPub){
    final Pair<Bytes, String> keyAndComment = unpackOpenSshRsaPublicKey(idRsaPub);
    return parseOpenSshRsaPublicKey(keyAndComment.getKey(), keyAndComment.getValue());
  }

  Pair<Bytes,String> unpackOpenSshRsaPublicKey(final String idRsaPub);

  OpenSshPublicKey parseOpenSshRsaPublicKey(final Bytes idRsaPubUnpacked, String comment);


  default RsaKeyPair parseOpenSshRsaPrivateKey(final String idRsa){
    return parseOpenSshRsaPrivateKey(unpackOpenSshRsaPrivateKey(idRsa));
  }

  Bytes unpackOpenSshRsaPrivateKey(final String idRsa);

  RsaKeyPair parseOpenSshRsaPrivateKey(final Bytes idRsaUnpacked);

  UnixSha512CryptHash unixSha512Crypt(String password);

  /**
   * <p>Common Unix salted password hash</p>
   * <p>See</p>
   * <ul>
   *   <li><a href="https://man7.org/linux/man-pages/man3/crypt.3.html">crypt(3) - Linux manual page</a></li>
   *   <li><a href="https://www.akkadia.org/drepper/SHA-crypt.txt">Unix crypt using SHA-256 and SHA-512 (Ulrich Drepper)</a></li>
   * </ul>
   */
  UnixSha512CryptHash unixSha512Crypt(Bytes password);

  Bytes convertToPem(Bytes certificate);

  default Bytes convertToPem(final X509Certificate certificate){
    return convertToPem(ByteUtils.newBytes(call(certificate::getEncoded)));
  }

}
