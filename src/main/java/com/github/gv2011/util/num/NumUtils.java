package com.github.gv2011.util.num;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.stream.IntStream;

import com.github.gv2011.util.ex.ThrowingRunnable;
import com.github.gv2011.util.tstr.TypedString.TypedStringParser;

public final class NumUtils {

  public static final Intg ZERO = SmallIntg.ZERO;
  public static final Intg ONE  = SmallIntg.ONE;
  public static final Intg TWO  = SmallIntg.TWO;
  public static final Intg THREE  = SmallIntg.THREE;

  static final BigInteger MIN_INT = BigInteger.valueOf(Integer.MIN_VALUE);
  static final BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);

  static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
  static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

  private NumUtils(){staticClass();}

  public static Decimal parse(final String dec){
    return num(new BigDecimal(dec));
  }

  public static Decimal parseComma(final String dec){
    return parse(dec.replace(".", "").replace(',', '.'));
  }

  public static Decimal num(final Number number){
    if(number instanceof BigDecimal) return num((BigDecimal) number);
    else if(number instanceof BigInteger) return intg((BigInteger) number);
    else if(
      number instanceof Integer ||
      number instanceof Byte ||
      number instanceof Long ||
      number instanceof Short
    ) return intg(number.longValue());
    else{
      return num(BigDecimal.valueOf(number.doubleValue()));
    }
  }

  public static Decimal num(final BigDecimal bigDecimal){
    return fromCanonical(BigDecimalUtils.canonical(bigDecimal));
  }

  public static Intg intg(final long l){
    return Integer.MIN_VALUE <= l && l <= Integer.MAX_VALUE
      ? intg((int)l)
      : intg(BigInteger.valueOf(l))
    ;
  }

  public static Intg intg(final BigInteger i){
    return intg(new BigDecimal(i));
  }

  public static Intg intg(final int i){
    return i!=Integer.MIN_VALUE ? SmallIntg.intg(i) : BigIntg.INTG_MIN_INT;
  }

  public static Intg intg(final BigDecimal bigDecimal){
    return fromCanonical(BigDecimalUtils.canonical(bigDecimal)).toIntg();
  }

  static Decimal fromCanonical(final BigDecimal canonical){
    assert BigDecimalUtils.isCanonical(canonical);
    return BigDecimalUtils.canonicalIsInteger(canonical)
      ?(BigDecimalUtils.isInIntRange(canonical) && !canonical.equals(BigDecimalUtils.MIN_INT)
        ? SmallIntg.intg(canonical.intValueExact())
        : BigIntg.from(canonical)
      )
      : new NonIntgDecimal(canonical)
    ;
  }

  public static boolean isOdd(final int n){
    return n%2==1;
  }

  public static String withLeadingZeros(final int i, final int digits){
    final StringBuilder sb = new StringBuilder(Integer.toString(Math.abs(i)));
    verify(sb.length()<=digits);
    while(sb.length()<digits) sb.insert(0, '0');
    if(i<0) sb.insert(0,'-');
    return sb.toString();
  }

  public static boolean isInt(final BigInteger i) {
    return i.compareTo(MIN_INT)>=0 && i.compareTo(MAX_INT)<=0;
  }

  public static boolean isLong(final BigInteger i) {
    return i.compareTo(MIN_LONG)>=0 && i.compareTo(MAX_LONG)<=0;
  }

  public static Intg zero() {
    return SmallIntg.ZERO;
  }

  public static int toInt(final long l) {
    verify(l>=Integer.MIN_VALUE && l<=Integer.MAX_VALUE);
    return (int)l;
  }

  public static IntStream stream(final int i) {
    return IntStream.range(0, i);
  }

  public static void doNTimes(final int i, final ThrowingRunnable operation) {
    verify(i>=0);
    call(()->{for(int j=0; j<i; j++) operation.runThrowing();});
  }

  public static final class DecimalParser implements TypedStringParser<Decimal>{
    @Override
    public Decimal parse(final String s) {
      return NumUtils.parse(s);
    }
  }

}
