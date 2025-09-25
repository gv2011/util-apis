package com.github.gv2011.util.sec;

import static com.github.gv2011.util.ex.Exceptions.call;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.beans.Computed;


public interface OpenSshPublicKey extends Bean{

  BigInteger modulus();

  BigInteger publicExponent();

  String comment();

  @Computed
  RSAPublicKeySpec rsaPublicKeySpec();

  @Computed
  RSAPublicKey rsaPublicKey();

  static RSAPublicKeySpec rsaPublicKeySpec(final OpenSshPublicKey key){
    return new RSAPublicKeySpec(key.modulus(), key.publicExponent());
  }

  static RSAPublicKey rsaPublicKey(final OpenSshPublicKey key){
    return (RSAPublicKey) call(()->KeyFactory.getInstance(SecUtils.RSA).generatePublic(key.rsaPublicKeySpec()));
  }
}
