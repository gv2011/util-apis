package com.github.gv2011.util.sec;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.icol.ICollections.listBuilder;
import static com.github.gv2011.util.icol.ICollections.listOf;
import static com.github.gv2011.util.icol.ICollections.toIList;
import static com.github.gv2011.util.num.NumUtils.withLeadingZeros;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.FileUtils;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.bytes.BytesBuilder;
import com.github.gv2011.util.bytes.Hash256;
import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.ex.ThrowingConsumer;
import com.github.gv2011.util.ex.ThrowingSupplier;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;

public final class SecUtils {

  public static final String TLSV12 = "TLSv1.2";
  public static final String JKS = "JKS";
  public static final String RSA = "RSA";
  public static final String JKS_DEFAULT_PASSWORD = "changeit";
  public static final String KEY_FILE_NAME = "key.pkcs8";
  public static final String PKCS12 = "PKCS12";
  public static final String PKCS12_FILE_EXTENSION = "p12";
  public static final String JAVAX_NET_DEBUG_SYS_PROP = "javax.net.debug";
  public static final String JAVAX_NET_DEBUG_SYS_PROP_ALL = "all";
  public static final String X_509 = "X.509";

  private static final String PKIX = "PKIX";
  private static final String SUN_X509 = "SunX509";
  private static final String CERT_FILE_PATTERN = "cert{}.crt";
  private static final String CERT_ALIAS = "cert";


  private SecUtils(){staticClass();}

  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(SecUtils.class);


  static final Constant<SecProvider> SEC_PROVIDER =
    RecursiveServiceLoader.lazyService(SecProvider.class)
  ;

  public static final SimpleKeyStore createSimpleKeyStore(final Domain domain){
    return SEC_PROVIDER.get().createSimpleKeyStore(domain);
  }

  public static final SimpleKeyStore loadSimpleKeyStore(final TypedBytes bytes){
    return SEC_PROVIDER.get().loadSimpleKeyStore(bytes);
  }

  /**
   * @param idRsaPub OpenSSH "id_rsa.pub" format
   */
  public static OpenSshPublicKey parseOpenSshRsaPublicKey(final String idRsaPub){
    return SEC_PROVIDER.get().parseOpenSshRsaPublicKey(idRsaPub);
  }

  /**
   * @param idRsa OpenSSH "id_rsa" format
   */
  public static RsaKeyPair parseOpenSshRsaPrivateKey(final String idRsa){
    return SEC_PROVIDER.get().parseOpenSshRsaPrivateKey(idRsa);
  }

  public static RSAPublicKey createRsaPublicKey(final BigInteger modulus, final BigInteger publicExponent){
    return (RSAPublicKey) call(()->
      KeyFactory.getInstance(RSA).generatePublic(new RSAPublicKeySpec(modulus, publicExponent))
    );
  }

  public static RSAPublicKey parseRsaPublicKey(final Bytes encodedKey){
    return (RSAPublicKey) readPublicKey(RSA, encodedKey);
  }

  public static RSAPrivateKey parseRsaPrivateKey(final Bytes encodedKey){
    return (RSAPrivateKey) readPrivateKey(RSA, encodedKey);
  }

  public static PublicKey readPublicKey(final String algorithm, final Bytes encodedKey){
    return call(()->KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(encodedKey.toByteArray())));
  }

  public static PrivateKey readPrivateKey(final String algorithm, final Bytes encodedKey){
    return call(()->KeyFactory.getInstance(algorithm).generatePrivate(new X509EncodedKeySpec(encodedKey.toByteArray())));
  }

  public static final X509Certificate readCertificate(final Bytes bytes){
    final CertificateFactory certFactory = call(()->CertificateFactory.getInstance(X_509));
    return callWithCloseable(
      bytes::openStream,
      s->(X509Certificate)certFactory.generateCertificate(s)
    );
  }

  public static final Hash256 getFingerPrint(final Certificate certificate){
    return ByteUtils.newBytes(call(certificate::getEncoded)).hash();
  }

  public static final X509Certificate readCertificateFromPem(final String pem){
    final CertificateFactory certFactory = call(()->CertificateFactory.getInstance(X_509));
    return callWithCloseable(
      ()->new ByteArrayInputStream(pem.getBytes(StandardCharsets.US_ASCII)),
      s->(X509Certificate)certFactory.generateCertificate(s)
    );
  }

  public static final CertificateChain readCertificateChainFromPem(final String pem){
    final CertificateFactory certFactory = call(()->CertificateFactory.getInstance(X_509));
    return callWithCloseable(
      ()->new ByteArrayInputStream(pem.getBytes(StandardCharsets.US_ASCII)),
      s->{
        final IList.Builder<X509Certificate> b = listBuilder();
        b.add((X509Certificate)certFactory.generateCertificate(s));
        b.add((X509Certificate)certFactory.generateCertificate(s));
        return createCertificateChain(b.build());
      }
    );
  }

  public static final void writeCertificateChain(final CertificateChain certChain, final Path folder){
    writeCertificateChain(certChain, folder, CERT_FILE_PATTERN);
  }

  public static final void writeCertificateChain(final CertificateChain certChain, final Path folder, final String certFilePattern){
    int i=0;
    boolean done = false;
    while(!done){
      final Path certFile = certFile(folder, i, certFilePattern);
      final boolean deleted = FileUtils.deleteFile(certFile);
      if(!deleted && i>=certChain.certificates().size()) done=true;
      i++;
    }
    for(i=0; i<certChain.certificates().size(); i++){
      final X509Certificate cert = certChain.certificates().get(i);
      ByteUtils.newBytes(call(cert::getEncoded)).write(certFile(folder,i, certFilePattern));
    }
  }

  public static final Bytes convertToPkcs12(final Path folder){
    return convertToPkcs12(
      RsaKeyPair.parsePkcs8(ByteUtils.read(folder.resolve("key.rsa"))),
      readCertificateChain(folder)
    );
  }

  public static final Bytes convertToPkcs12(final RsaKeyPair keyPair, final CertificateChain chain){
    verifyEqual(chain.leafCertificate().getPublicKey(), keyPair.getPublic());
    return call(()->{
      final KeyStore ks = KeyStore.getInstance(SecUtils.PKCS12);
      ks.load(null, null);
      ks.setKeyEntry(
        CERT_ALIAS,
        keyPair.getPrivate(),
        null,
        chain.certificates().toArray(new Certificate[chain.certificates().size()])
      );
      final BytesBuilder bytesBuilder = ByteUtils.newBytesBuilder();
      ks.store(bytesBuilder, "".toCharArray());
      return bytesBuilder.build();
    });
  }

  public static final Bytes convertToPem(final X509Certificate certificate){
    return SEC_PROVIDER.get().convertToPem(certificate);
  }

  public static final CertificateChain readCertificateChain(final Path folder){
    return readCertificateChain(folder, CERT_FILE_PATTERN);
  }

  public static final CertificateChain readCertificateChain(final Path folder, final String certFilePattern){
    final IList.Builder<X509Certificate> chain = listBuilder();
    int i = 0;
    Path certFile = certFile(folder, i, certFilePattern);
    while(Files.exists(certFile)){
      chain.add(readCertificate(ByteUtils.read(certFile)));
      certFile = certFile(folder, ++i, certFilePattern);
    }
    return createCertificateChain(chain.build());
  }

  private static Path certFile(final Path folder, final int i) {
    return certFile(folder, i, CERT_FILE_PATTERN);
  }

  private static Path certFile(final Path folder, final int i, final String certFilePattern) {
    return folder.resolve(format(certFilePattern,withLeadingZeros(i+1,2)));
  }


  public static final KeyStore readKeyStore(final ThrowingSupplier<InputStream> streamSupplier){
    final KeyStore ks = call(()->KeyStore.getInstance(JKS));
    callWithCloseable(
      streamSupplier,
      (ThrowingConsumer<InputStream>)s->ks.load(s, JKS_DEFAULT_PASSWORD.toCharArray())
    );
    return ks;
  }

  public static final KeyStore createJKSKeyStore(
    final RsaKeyPair privKey, final CertificateChain certChain
  ){
    final KeyStore keyStore = call(()->KeyStore.getInstance(JKS));
    call(()->keyStore.load(null));
    return addToKeyStore(privKey, certChain, keyStore, Opt.empty());
  }

  public static final KeyStore createJKSKeyStore(final Path certificateDirectory){
    return createJKSKeyStore(
      RsaKeyPair.parsePkcs8(ByteUtils.read(certificateDirectory.resolve(KEY_FILE_NAME))),
      createCertificateChain(listOf(readCertificate(ByteUtils.read(certFile(certificateDirectory, 0)))))
    );
  }

  public static final KeyStore addToKeyStore(final ServerCertificate serverCertificate, final KeyStore keystore){
    addToKeyStore(
      serverCertificate.keyPair(),
      serverCertificate.certificateChain(),
      keystore,
      Opt.of(serverCertificate.domain().toString())
    );
    return keystore;
  }

  public static final KeyStore addToKeyStore(
    final RsaKeyPair privKey, final CertificateChain certChain, final KeyStore keystore, final Opt<String> alias
  ){
    verifyEqual(
      privKey.getPublic(), certChain.leafCertificate().getPublicKey(),
      (e,a)->format("The provided key and certificate chain do not match.\nChain key: {}\nPublic key: {}.", e, a)
    );
    call(()->keystore.setKeyEntry(
      alias.orElseGet(()->findAlias(keystore)),
      privKey.getPrivate(),
      JKS_DEFAULT_PASSWORD.toCharArray(),
      certChain.certificates().toArray(new Certificate[certChain.certificates().size()])
    ));
    return keystore;
  }

  private static final String findAlias(final KeyStore ks){
    return call(()->{
      String alias = CERT_ALIAS;
      int i=0;
      while(ks.containsAlias(alias)) {
        alias = CERT_ALIAS+(++i);
      }
      return alias;
    });
  }

  public static final Bytes createJKSKeyStoreBytes(
    final RsaKeyPair privKey, final CertificateChain certChain
  ){
    Bytes result;
    try(BytesBuilder builder = ByteUtils.newBytesBuilder()){
      call(()->createJKSKeyStore(privKey, certChain).store(builder, JKS_DEFAULT_PASSWORD.toCharArray()));
      result = builder.build();
    }
    return result;
  }

  public static final Bytes createJKSKeyStore(final X509Certificate trustedCertificate){
    final KeyStore keystore = call(()->KeyStore.getInstance(JKS));
    call(()->keystore.load(null, null));
    call(()->keystore.setCertificateEntry(CERT_ALIAS, trustedCertificate));
    Bytes result;
    try(BytesBuilder builder = ByteUtils.newBytesBuilder()){
      call(()->keystore.store(builder, JKS_DEFAULT_PASSWORD.toCharArray()));
      result = builder.build();
    }
    return result;
  }

  public static final void extractJKSKeyStore(final Bytes keyStore, final Path folder){
    final KeyStore ks = readKeyStore(keyStore::openStream);
    final RsaKeyPair privKey = RsaKeyPair.create(
      (RSAPrivateCrtKey)call(()->ks.getKey(CERT_ALIAS, JKS_DEFAULT_PASSWORD.toCharArray()))
    );
    final CertificateChain chain = createCertificateChain(
      Arrays.stream(call(()->ks.getCertificateChain(CERT_ALIAS)))
      .map(c->(X509Certificate)c)
      .collect(toIList())
    );
    privKey.encode().write(folder.resolve(KEY_FILE_NAME));
    writeCertificateChain(chain, folder);
  }

  public static final SSLServerSocketFactory createServerSocketFactory(final Bytes keyStore){
    return createServerSocketFactory(readKeyStore(keyStore::openStream), false);
  }

  public static final SSLServerSocketFactory createServerSocketFactory(
    final KeyStore keyStore, final boolean trustAll
  ){
    return call(()->{
      final SSLContext sslContext = call(()->SSLContext.getInstance(TLSV12));
      sslContext.init(
        getKeyManagers(keyStore),
        trustAll ? new TrustManager[]{new TrustAllTrustManager()} : null,
        null
      );
      return sslContext.getServerSocketFactory();
    });
  }

  private static final KeyManager[] getKeyManagers(final KeyStore keyStore){
    return call(()->{
      final KeyManagerFactory kmf = KeyManagerFactory.getInstance(SUN_X509);
      kmf.init(keyStore, SecUtils.JKS_DEFAULT_PASSWORD.toCharArray());
      return kmf.getKeyManagers();
    });
  }

  public static final SSLServerSocketFactory createServerSocketFactory(final Path certificateDirectory){
    return createServerSocketFactory(certificateDirectory, false);
  }

  public static final SSLServerSocketFactory createServerSocketFactory(
    final Path certificateDirectory, final boolean trustAll
  ){
    createCertificateIfMissing(certificateDirectory);
    return createServerSocketFactory(
      createJKSKeyStore(certificateDirectory),
      trustAll
    );
  }

  private static final void createCertificateIfMissing(final Path certificateDirectory){
    call(()->{
      Files.createDirectories(certificateDirectory);
      final Path keyFile = certificateDirectory.resolve(KEY_FILE_NAME);
      final Path certFile = certFile(certificateDirectory, 0);
      if(!Files.exists(keyFile)){
        verify(!Files.exists(certFile));
        RsaKeyPair.create().encode().write(keyFile);
      }
      if(!Files.exists(certFile)){
        ByteUtils.newBytes(
          SEC_PROVIDER.get().createCertificateBuilder().build(RsaKeyPair.parsePkcs8(ByteUtils.read(keyFile))).getEncoded()
        ).write(certFile);
      }
    });
  }

  public static final SSLSocket connect(
    final Path certificateDirectory, final PublicKey peer, final InetSocketAddress address
  ){
    createCertificateIfMissing(certificateDirectory);
    return call(()->{
      final SSLContext sslContext = call(()->SSLContext.getInstance(TLSV12));
      sslContext.init(
        getKeyManagers(createJKSKeyStore(certificateDirectory)),
        new TrustManager[]{new TrustAllTrustManager()},
        null
      );
      boolean success = false;
      final Socket s = sslContext.getSocketFactory().createSocket(address.getAddress(), address.getPort());
      try{
        final SSLSocket socket = (SSLSocket) s;
        verifyEqual(socket.getSession().getPeerCertificates()[0].getPublicKey(), peer);
        success = true;
        return socket;
      }
      finally{
        if(!success) s.close();
      }
    });
  }

  public static final SSLSocketFactory createSocketFactory(final X509Certificate trustedCertificate){
    return call(()->{
      final KeyStore ks = KeyStore.getInstance(JKS);
      ks.load(null, null);
      ks.setCertificateEntry("cert", trustedCertificate);
      final TrustManagerFactory tmf = TrustManagerFactory.getInstance(PKIX);
      tmf.init(ks);
      final SSLContext sslContext = call(()->SSLContext.getInstance(TLSV12));
      sslContext.init(null, tmf.getTrustManagers() , null);
      return sslContext.getSocketFactory();
    });
  }

  public static CertificateChain createCertificateChain(final IList<X509Certificate> certificates){
    return new CertificateChainImp(certificates);
  }

  public static RSAPublicKey getPublicKey(final Path certificateDirectory) {
    createCertificateIfMissing(certificateDirectory);
    return RsaKeyPair.parsePkcs8(ByteUtils.read(certificateDirectory.resolve(KEY_FILE_NAME))).getPublic();
  }

  public static final DestroyingCloseable asDestroyable(final KeyStore keyStore){
    return new KeyStoreDestroyer(keyStore);
  }

}
