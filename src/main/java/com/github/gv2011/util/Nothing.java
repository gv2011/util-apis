package com.github.gv2011.util;

import com.github.gv2011.util.icol.IEmpty;

@Deprecated//Use com.github.gv2011.util.icol.Nothing
public class Nothing extends IEmpty<Object> implements Parsable{
  
  @Deprecated//Use com.github.gv2011.util.icol.Nothing
  public static final Nothing INSTANCE = new Nothing();

  @Deprecated//Use com.github.gv2011.util.icol.Nothing
  public static Nothing parse(final CharSequence cs) {
    return com.github.gv2011.util.icol.Nothing.parse(cs);
  }

  @Deprecated//Use com.github.gv2011.util.icol.Nothing
  protected Nothing() {
  }

  public static Nothing nothing() {
    return INSTANCE;
  }

}
