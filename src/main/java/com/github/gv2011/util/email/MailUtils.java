package com.github.gv2011.util.email;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.util.function.Consumer;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.Constant;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;

public final class MailUtils {
  
  private MailUtils(){staticClass();}

  static final Constant<MailProvider> MAIL_PROVIDER =
    RecursiveServiceLoader.lazyService(MailProvider.class)
  ;
  
  public static AutoCloseableNt createMailListener(Consumer<Email> mailReceiver, MailAccount mailAccount){
    return MAIL_PROVIDER.get().createMailListener(mailReceiver, mailAccount);
  }

}
