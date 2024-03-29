package com.github.gv2011.util;

import static com.github.gv2011.util.Verify.verify;

public enum CharacterType {
  UNASSIGNED,
  UPPERCASE_LETTER,
  LOWERCASE_LETTER,
  TITLECASE_LETTER,
  MODIFIER_LETTER,
  OTHER_LETTER,
  NON_SPACING_MARK,
  ENCLOSING_MARK,
  COMBINING_SPACING_MARK,
  DECIMAL_DIGIT_NUMBER,
  LETTER_NUMBER,
  OTHER_NUMBER,
  SPACE_SEPARATOR,
  LINE_SEPARATOR,
  PARAGRAPH_SEPARATOR,
  CONTROL,
  FORMAT,
  UNKNOWN,
  PRIVATE_USE,
  SURROGATE,
  DASH_PUNCTUATION,
  START_PUNCTUATION,
  END_PUNCTUATION,
  CONNECTOR_PUNCTUATION,
  OTHER_PUNCTUATION,
  MATH_SYMBOL,
  CURRENCY_SYMBOL,
  MODIFIER_SYMBOL,
  OTHER_SYMBOL,
  INITIAL_QUOTE_PUNCTUATION,
  FINAL_QUOTE_PUNCTUATION;

  public static final CharacterType forInt(final int i){
    verify(i>=0 && i<=FINAL_QUOTE_PUNCTUATION.ordinal() && i!=UNKNOWN.ordinal());
    return CharacterType.values()[i];
  }

  public static final CharacterType ofCodepoint(final int cp){
    return forInt(Character.getType(cp));
  }

  public final boolean contains(final int codepoint){
    return Character.getType(codepoint)==ordinal();
  }
}
