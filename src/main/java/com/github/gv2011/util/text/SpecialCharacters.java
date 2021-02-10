package com.github.gv2011.util.text;

import static com.github.gv2011.util.Verify.verifyEqual;

/**
 * @see <a href="https://www.unicode.org/standard/reports/tr13/tr13-5.html">Unicode Newline Guidelines</a>
 */
public enum SpecialCharacters {
  
  NEL("next line", "\u0085", ""),
  LS("Line Separator", "\u2028", null),
  PS("Paragraph Separator", "\u2029", null);
  
  private String longName;
  private String string;

  private SpecialCharacters(String longName, String escaped, String literal){
    this.longName = longName;
    this.string = escaped;
    if(literal!=null) verifyEqual(literal, escaped);
  }

  @Override
  public String toString() {
    return string;
  }
  
  public String longName(){
    return longName;
  }
}
