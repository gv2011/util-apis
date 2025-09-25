package com.github.gv2011.util.ssh;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.security.PublicKey;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.RsaKeyPair;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;
import com.github.gv2011.util.ssh.SshConnection.Factory;

public final class SshUtils {

  private SshUtils(){staticClass();}

  private static final Constant<Factory> SSH_CONNECTION_FACTORY =
    RecursiveServiceLoader.lazyService(SshConnection.Factory.class)
  ;

  public static final SshConnection.Factory sshConnectionFactory(){return SSH_CONNECTION_FACTORY.get();}

  public static final SshConnection connect(
    final Domain host, final Opt<Integer> port, final Opt<PublicKey> hostKey, final User user, final RsaKeyPair userKey
  ) throws AuthenticationFailedException {
    return sshConnectionFactory().connect(host, port, hostKey, user, userKey);
  }

}
