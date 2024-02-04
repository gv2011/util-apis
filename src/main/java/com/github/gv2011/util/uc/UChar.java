package com.github.gv2011.util.uc;

import java.lang.Character.UnicodeScript;

import com.github.gv2011.util.CharacterType;

public interface UChar extends Comparable<UChar>{

  static final int MAX_ISO = 0x100-1;
  static final int MAX_BMP = 0x10000-1;

  static final UChar REPLACEMENT_CHARACTER = UCharImp.REPLACEMENT_CHARACTER;

  String name();

  int codePoint();

  CharacterType type();

  UnicodeScript script();

  default boolean ascii(){
    return codePoint()<128;
  }

  boolean isIso8859_1Character();

  boolean isBmpCharacter();

  boolean inBaseSet();

  String printable();

}
