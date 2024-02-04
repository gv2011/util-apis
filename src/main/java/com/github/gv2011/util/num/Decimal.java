package com.github.gv2011.util.num;

import static com.github.gv2011.util.num.NumUtils.intg;

import java.math.BigDecimal;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.beans.DefaultValue;
import com.github.gv2011.util.beans.ParserClass;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.num.NumUtils.DecimalParser;
import com.github.gv2011.util.tstr.TypedString;

@ParserClass(DecimalParser.class)
@DefaultValue("0")
public interface Decimal extends TypedString<Decimal>{

  BigDecimal toBigDecimal();

  default int toInt(){
    return toBigDecimal().intValueExact();
  }

  Intg toIntg();

  /**
   * see <a href="https://262.ecma-international.org/11.0/#sec-numeric-types-number-tostring">ECMAScript 6.1.6.1.20
   * Number::toString(x)</a>
   */
  String toEcmaString();

  Intg minusOne();
  Intg zero();
  Intg one();
  Intg two();
  Intg ten();

  Decimal negate();

  int signum();

  boolean gt (Decimal other);
  boolean gte(Decimal other);
  boolean lt (Decimal other);
  boolean lte(Decimal other);

  default Decimal abs(){
    return signum()==-1 ? negate() : this;
  }

  Decimal plus(Decimal dec);

  default Decimal plus(final Intg i){
    return plus((Decimal)i);
  }

  default Decimal plusOne(){
    return plus(one());
  }


  default Decimal minus(final Decimal dec){
    return plus(dec.negate());
  }

  default Decimal minus(final Intg i){
    return minus((Decimal)i);
  }

  Decimal multiply(Decimal factor);

  default Decimal multiply(final Intg factor){
    return multiply((Decimal) factor);
  }

  Pair<Intg,Intg> divR(Decimal divisor);

  default boolean isNegative(){
    return signum()<0;
  }

  boolean isZero();

  default boolean isOne() {return equals(one());}

  default boolean isTwo() {return equals(two());}

  boolean isIntg();

  boolean fitsInt();
  int intValue();

  boolean fitsLong();
  long longValue();

  double doubleValue();

  IList<Intg> toBaseIntg(Intg base);

  IList<Intg> toBaseIntg(Intg base, Intg digits);

  Pair<IList<Intg>,Intg> toBase(Intg base);

  default boolean isMultipleOf(final Decimal q){
    return divR(q).getValue().isZero();
  }

  default Intg precision(){
    return intg(toBigDecimal().precision());
  }

  default Decimal min(final Intg other) {
    return min((Decimal) other);
  }

  default Decimal min(final Decimal other) {
    return lte(other) ? this : other;
  }


}
