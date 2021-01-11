package com.github.gv2011.util.uc;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;

public final class TextUtils {
  
  private TextUtils(){staticClass();}


  public static final Constant<UnicodeProvider> UNICODE_PROVIDER =
    RecursiveServiceLoader.lazyService(UnicodeProvider.class, JdkUnicodeProvider::new)
  ;

}
