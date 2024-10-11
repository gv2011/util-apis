package com.github.gv2011.util.sec;

import java.math.BigInteger;
import java.security.spec.RSAPublicKeySpec;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.beans.Computed;


public interface OpenSshRsaPublicKey extends Bean{

  BigInteger modulus();
  BigInteger publicExponent();

  String comment();

  @Computed
  RSAPublicKeySpec rsaPublicKeySpec();


  static RSAPublicKeySpec rsaPublicKeySpec(final OpenSshRsaPublicKey key){
    return new RSAPublicKeySpec(key.modulus(), key.publicExponent());
  }

}
