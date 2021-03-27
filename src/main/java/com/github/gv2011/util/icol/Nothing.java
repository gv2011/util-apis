package com.github.gv2011.util.icol;

import static com.github.gv2011.util.Verify.verifyEqual;

import java.util.Locale;

/**
 * Replacement for void/Void in some situations (avoids null). Example: allows
 * to treat consumer and suppliers formally as functions.
 */
@SuppressWarnings("deprecation")
public class Nothing extends com.github.gv2011.util.Nothing{
  
  private static final String STRING_VALUE = "NOTHING".intern();

  public static final Nothing INSTANCE = new Nothing();

  public static Nothing parse(final CharSequence cs) {
    verifyEqual(cs.toString().toUpperCase(Locale.ROOT), STRING_VALUE);
    return INSTANCE;
  }

  private Nothing() {
  }

  public static final Nothing nothing() {
    return INSTANCE;
  }

  @Override
  public String toString() {
    return STRING_VALUE;
  }

}
