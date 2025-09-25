package com.github.gv2011.util.sec;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.sec.SecUtils.RSA;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

import com.github.gv2011.util.beans.NoDefaultValue;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.tstr.AbstractTypedString;

@NoDefaultValue
public final class RsaKeyPair extends AbstractTypedString<RsaKeyPair> implements Destroyable{

  public static final RsaKeyPair create(final KeyPair keyPair){
    final RSAPrivateCrtKey priv = (RSAPrivateCrtKey)keyPair.getPrivate();
    final RSAPublicKey pub = (RSAPublicKey)keyPair.getPublic();
    check(pub, priv);
    return create(priv);
  }

  public static final RsaKeyPair create(final RSAPrivateCrtKey priv){
    return new RsaKeyPair(priv);
  }

  public static final RsaKeyPair create(){
    final KeyPairGenerator keyGen = call(()->KeyPairGenerator.getInstance(RSA));
    keyGen.initialize(4096);
    return RsaKeyPair.create(keyGen.generateKeyPair());
  }

  public static final RsaKeyPair parsePkcs8(final Bytes pkcs8){
    return RsaKeyPair.create(parseRSAPrivateCrtKey(pkcs8));
  }

  private static final RSAPrivateCrtKey parseRSAPrivateCrtKey(final Bytes pkcs8){
    final PKCS8EncodedKeySpec spec =  new PKCS8EncodedKeySpec(pkcs8.toByteArray());
    return (RSAPrivateCrtKey)call(()->KeyFactory.getInstance(RSA).generatePrivate(spec));
  }

  public static final void check(final RSAPublicKey pub, final RSAPrivateCrtKey priv){
    verifyEqual(pub.getModulus(), priv.getModulus());
    verifyEqual(pub.getPublicExponent(), priv.getPublicExponent());
  }


  private final RSAPrivateCrtKey priv;

  public RsaKeyPair(final String pkcs8base64){
    this(parseRSAPrivateCrtKey(ByteUtils.asUtf8(pkcs8base64).content().decodeBase64()));
  }

  private RsaKeyPair(final RSAPrivateCrtKey priv) {
    this.priv = priv;
  }

  public final String getKeyType(){
    return RSA;
  }

  public RSAPrivateCrtKey getPrivate(){
    verify(!isDestroyed());
    return priv;
  }

  public RSAPublicKey getPublic(){
    verify(!isDestroyed());
    return SecUtils.createRsaPublicKey(priv.getModulus(), priv.getPublicExponent());
  }

  @Override
  public void destroy() throws DestroyFailedException {
    priv.destroy();
  }

  @Override
  public boolean isDestroyed() {
    return priv.isDestroyed();
  }

  @Override
  public int hashCode() {
    return RsaKeyPair.class.hashCode() * 31 + getPublic().hashCode();
  }

  public boolean isAequivalent(final RsaKeyPair other) {
    return isAequivalent(other.getPublic());
  }

  public boolean isAequivalent(final RSAPublicKey publicKey) {
    final RSAPublicKey thisPub = getPublic();
    return thisPub.getModulus().equals(publicKey.getModulus())
        && thisPub.getPublicExponent().equals(publicKey.getPublicExponent())
    ;
  }

  public Bytes encode(){
    return ByteUtils.newBytes(priv.getEncoded());
  }

  public KeyPair asKeyPair(){
    return new KeyPair(getPublic(), priv);
  }

  @Override
  public RsaKeyPair self() {
    return this;
  }

  @Override
  public Class<RsaKeyPair> clazz() {
    return RsaKeyPair.class;
  }

  @Override
  public String toString() {
    return encode().toBase64().utf8ToString();
  }

}
