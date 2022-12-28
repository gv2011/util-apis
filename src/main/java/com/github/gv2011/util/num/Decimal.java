package com.github.gv2011.util.num;

import java.math.BigDecimal;

import com.github.gv2011.util.beans.DefaultValue;
import com.github.gv2011.util.beans.ParserClass;
import com.github.gv2011.util.num.NumUtils.DecimalParser;
import com.github.gv2011.util.tstr.TypedString;

@ParserClass(DecimalParser.class)
@DefaultValue("0")
public interface Decimal extends TypedString<Decimal>{

  BigDecimal toBigDecimal();

  int toInt();

  /**
   * see <a href="https://262.ecma-international.org/11.0/#sec-numeric-types-number-tostring">ECMAScript 6.1.6.1.20
   * Number::toString(x)</a>
   */
  String toEcmaString();

  Decimal zero();

  Decimal one();

  Decimal negate();

  int signum();

  default Decimal abs(){
    return signum()==-1 ? negate() : this;
  }

  Decimal plus(Decimal dec);

  default Decimal minus(final Decimal dec){
    return plus(dec.negate());
  }

  boolean isInteger();

  boolean fitsInt();
  int intValue();

  boolean fitsLong();
  long longValue();

  double doubleValue();
}
