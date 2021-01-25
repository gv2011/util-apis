package com.github.gv2011.util.sec;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.icol.ICollections.pathFrom;
import static java.util.stream.Collectors.joining;

import java.security.Principal;
import java.util.Locale;

import com.github.gv2011.util.StringUtils;
import com.github.gv2011.util.beans.DefaultValue;
import com.github.gv2011.util.icol.Path;
import com.github.gv2011.util.net.NetUtils;
import com.github.gv2011.util.tstr.AbstractTypedString;
import com.github.gv2011.util.uc.TextUtils;
import com.github.gv2011.util.uc.UnicodeProvider;

@DefaultValue("localhost")
public class Domain extends AbstractTypedString<Domain> {

  private static final UnicodeProvider PROV = TextUtils.UNICODE_PROVIDER.get();
  
  public static final Domain LOCALHOST = new Domain("localhost".intern());

  public static Domain parse(String domain) {
    domain = domain.toLowerCase(Locale.ROOT).trim();
    if(domain.equals(LOCALHOST.ascii)) return LOCALHOST;
    else{
      verify(!domain.isEmpty(), ()->"Domain is empty.");
      return new Domain(PROV.idnaNameToASCII(domain));
    }
  }

  public static Domain parse(Path path) {
    return parse(path.stream().collect(joining(".")));
  }
  
  public static Domain from(Principal principal) {
    return parse(StringUtils.removePrefix(principal.getName(), "CN="));
  }
  
  private final String ascii;

  private Domain(final String ascii) {
    this.ascii = ascii;
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
    return ascii;
  }

  public String toUnicode() {
    return PROV.idnaNameToUnicode(ascii);
  }

  public String toAscii() {
    return ascii;
  }
  
  public Path asPath(){
    return pathFrom(StringUtils.split(toUnicode(), '.'));
  }
  
  public boolean isInetAddress(){
    return NetUtils.isInetAddress(ascii);
  }

  public boolean isLocalhost() {
    return equals(LOCALHOST);
  }

}
