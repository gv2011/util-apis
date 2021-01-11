package com.github.gv2011.util.uc;

import java.net.IDN;

final class JdkUnicodeProvider implements UnicodeProvider{

  @Override
  public String idnaNameToASCII(CharSequence name) {
    return IDN.toASCII(name.toString());
  }

  @Override
  public String idnaNameToUnicode(CharSequence name) {
    return IDN.toUnicode(name.toString());
  }
  
}
