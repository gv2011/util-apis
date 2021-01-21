package com.github.gv2011.util.uc;

import java.net.IDN;

/**
 * JDK is badly outdated by still using IDNA 2003 
 * (see <a href="https://bugs.openjdk.java.net/browse/JDK-6988055">JDK-6988055</a>).
 * For IDNA 2008 use <a href="https://github.com/unicode-org/icu">com.ibm.icu:icu4j</a>,
 * see com.github.gv2011.http.imp.IcuUnicodeProvider.
 */
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
