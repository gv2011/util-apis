package com.github.gv2011.util.uc;

public interface UnicodeProvider {
  
  /**
   * Converts a whole domain name into its ASCII form for DNS lookup.
   */
  String idnaNameToASCII(CharSequence name);

  /**
   * Converts a whole domain name into its Unicode form for human-readable display.
   */
  String idnaNameToUnicode(CharSequence name);

}
