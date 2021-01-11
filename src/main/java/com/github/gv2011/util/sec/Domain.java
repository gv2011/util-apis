package com.github.gv2011.util.sec;

import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;

import java.security.Principal;
import java.util.Locale;

import com.github.gv2011.util.StringUtils;
import com.github.gv2011.util.beans.DefaultValue;
import com.github.gv2011.util.tstr.AbstractTypedString;
import com.github.gv2011.util.uc.TextUtils;
import com.github.gv2011.util.uc.UnicodeProvider;

@DefaultValue("localhost")
public class Domain extends AbstractTypedString<Domain> {

  private static final UnicodeProvider PROV = TextUtils.UNICODE_PROVIDER.get();
  
  public static final Domain LOCALHOST = new Domain("localhost");

  public static Domain parse(String domain) {
    return domain.equals(LOCALHOST.domain) ? LOCALHOST : new Domain(domain.toLowerCase(Locale.ROOT).trim());
  }

  public static Domain from(Principal principal) {
    return parse(StringUtils.removePrefix(principal.getName(), "CN="));
  }
  
//  private static final int IDN_FLAGS = IDN.ALLOW_UNASSIGNED | IDN.USE_STD3_ASCII_RULES;
  

  private final String domain;

  public Domain(final String domain) {
    verify(!domain.isEmpty());
    verifyEqual(domain, domain.toLowerCase(Locale.ROOT).trim());
    final int i = domain.indexOf('|');
    if(i==-1){
      this.domain = notNull(PROV).idnaNameToASCII(domain);
    }
    else{
      final String ascii = domain.substring(0, i);
      final String idnAscii = PROV.idnaNameToASCII(domain.substring(i+1));
      if(idnAscii.equals(ascii)) this.domain = idnAscii;
      else this.domain = domain;
    }
  }

  @Override
  public Domain self() {
    return this;
  }

  @Override
  public Class<Domain> clazz() {
    return Domain.class;
  }

  @Override
  public String toString() {
    return domain;
  }

  public String toUnicode() {
    final int i = domain.indexOf('|');
    return i == -1 ? PROV.idnaNameToUnicode(domain) : domain.substring(i+1);
  }

  public String toAscii() {
    final int i = domain.indexOf('|');
    return i==-1 ? domain : domain.substring(0, i);
  }

}
