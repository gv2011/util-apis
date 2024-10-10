package com.github.gv2011.util.uc;

import static com.github.gv2011.util.CharacterType.COMBINING_SPACING_MARK;
import static com.github.gv2011.util.CharacterType.DASH_PUNCTUATION;
import static com.github.gv2011.util.CharacterType.FORMAT;
import static com.github.gv2011.util.CharacterType.LINE_SEPARATOR;
import static com.github.gv2011.util.CharacterType.MODIFIER_LETTER;
import static com.github.gv2011.util.CharacterType.MODIFIER_SYMBOL;
import static com.github.gv2011.util.CharacterType.NON_SPACING_MARK;
import static com.github.gv2011.util.CharacterType.PARAGRAPH_SEPARATOR;
import static com.github.gv2011.util.CharacterType.SPACE_SEPARATOR;
import static com.github.gv2011.util.CharacterType.SURROGATE;
import static com.github.gv2011.util.ex.Exceptions.notYetImplemented;
import static com.github.gv2011.util.icol.ICollections.setOf;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static java.lang.Character.DIRECTIONALITY_COMMON_NUMBER_SEPARATOR;
import static java.lang.Character.DIRECTIONALITY_EUROPEAN_NUMBER;
import static java.lang.Character.DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR;
import static java.lang.Character.DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR;
import static java.lang.Character.DIRECTIONALITY_LEFT_TO_RIGHT;
import static java.lang.Character.DIRECTIONALITY_OTHER_NEUTRALS;
import static java.lang.Character.UnicodeScript.COMMON;
import static java.lang.Character.UnicodeScript.LATIN;

import java.lang.Character.UnicodeScript;
import java.util.Collection;
import java.util.function.IntPredicate;

import com.github.gv2011.util.CharacterType;
import com.github.gv2011.util.ex.ThrowingFunction;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;

final class SetOfCharsImp implements SetOfChars{

  IntPredicate predicate;

  SetOfCharsImp(final IntPredicate predicate) {
    this.predicate = predicate;
  }

  @Override
  public boolean containsChar(final int codePoint) {
    return predicate.test(codePoint);
  }

  @Override
  public Opt<UChar> tryGetLower(final UChar e) {
    return notYetImplemented();
  }

  @Override
  public Opt<UChar> tryGetFloor(final UChar e) {
    return notYetImplemented();
  }

  @Override
  public Opt<UChar> tryGetCeiling(final UChar e) {
    return notYetImplemented();
  }

  @Override
  public Opt<UChar> tryGetHigher(final UChar e) {
    return notYetImplemented();
  }

  @Override
  public SetOfChars subSet(final UChar from, final boolean fromInclusive, final UChar to, final boolean toInclusive) {
    return notYetImplemented();
  }

  @Override
  public SetOfChars intersection(final Collection<?> other) {
    return notYetImplemented();
  }

  @Override
  public SetOfChars addElement(final UChar other) {
    return notYetImplemented();
  }

  @Override
  public final <F> ISet<F> map(final ThrowingFunction<? super UChar, ? extends F> mapping) {
    return stream().map(mapping.asFunction()).collect(toISet());
  }


  private static final ISet<UnicodeScript> LS_SCRIPTS = setOf(COMMON, LATIN);
  private static final ISet<Byte> DIRECTIONALITIES = setOf(
    DIRECTIONALITY_LEFT_TO_RIGHT,
    DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR,
    DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR,
    DIRECTIONALITY_OTHER_NEUTRALS,
    DIRECTIONALITY_EUROPEAN_NUMBER,
    DIRECTIONALITY_COMMON_NUMBER_SEPARATOR
  );
  private static final ISet<CharacterType> LS_EXCLUDED_TYPES = setOf(
    NON_SPACING_MARK, DASH_PUNCTUATION, FORMAT, COMBINING_SPACING_MARK,
    MODIFIER_LETTER, MODIFIER_SYMBOL, LINE_SEPARATOR, PARAGRAPH_SEPARATOR,
    SPACE_SEPARATOR, SURROGATE
  );
  private static final ISet<Integer> ALLOWED = setOf(
    "-".codePointAt(0) //width of other DASH_PUNCTUATION members is problematic
  );

  static final boolean isLatinSimple(final int cp){
    return
      ALLOWED.contains(cp) ||
      (
        LS_SCRIPTS.contains(UnicodeScript.of(cp)) &&
        !LS_EXCLUDED_TYPES.contains(CharacterType.ofCodepoint(cp)) &&
        DIRECTIONALITIES.contains(Character.getDirectionality(cp))
      )
    ;
  }

}
