package com.github.gv2011.util.ssh;

import java.security.PublicKey;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.Constant;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.RsaKeyPair;
import com.github.gv2011.util.sec.SecProvider;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;

public interface SshConnection extends AutoCloseableNt{

  static final Constant<SecProvider> SSH_CONNECTION =
    RecursiveServiceLoader.lazyService(SecProvider.class)
  ;

  static interface Factory{
    SshConnection connect(
      final Domain host, Opt<Integer> port, final Opt<PublicKey> hostKey, final User user, final RsaKeyPair userKey
    ) throws AuthenticationFailedException;
  }

}
