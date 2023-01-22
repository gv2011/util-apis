package com.github.gv2011.util.icol;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;

import java.util.HashSet;
import java.util.Locale;

/**
 * Replacement for void/Void in some situations (avoids null). Example: allows
 * to treat consumer and suppliers formally as functions.
 */
@SuppressWarnings("rawtypes")
public interface Nothing extends Empty{

  public static final String STRING_VALUE = Nothing.class.getSimpleName().intern();
  public static final int HASH_CODE = verify(new HashSet().hashCode(), i->i.intValue()==0);

  public static Nothing parse(final CharSequence cs) {
    verifyEqual(cs.toString().toUpperCase(Locale.ROOT), STRING_VALUE);
    return ICollections.nothing();
  }

  @SuppressWarnings("unchecked")
  default <E> Opt<E> asSet(){
    return this;
  }

}
