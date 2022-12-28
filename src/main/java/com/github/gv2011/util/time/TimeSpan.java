package com.github.gv2011.util.time;

import static com.github.gv2011.util.Comparison.max;
import static com.github.gv2011.util.Comparison.min;
import static com.github.gv2011.util.Verify.verify;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;

import com.github.gv2011.util.beans.NoDefaultValue;
import com.github.gv2011.util.beans.ParserClass;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.tstr.TypedString;

@ParserClass(TimeSpan.TimeSpanParser.class)
@NoDefaultValue
public interface TimeSpan extends TypedString<TimeSpan>{

  public static final Comparator<TimeSpan> COMPARATOR = Comparator
    .comparing(TimeSpan::from).thenComparing(TimeSpan::until)
  ;

  public static final class TimeSpanParser implements TypedStringParser<TimeSpan>{
    @Override
    public final TimeSpan parse(final String s) {
      return TimeSpanImp.parse(s);
    }
  }

  public static TimeSpan create(final Instant from, final Instant until){
    verify(!until.isBefore(from));
    return new TimeSpanImp(from, until);
  }

  public static TimeSpan parse(final String timeSpanString){
    return TimeSpanImp.parse(timeSpanString);
  }

  Instant from();

  Instant until();

  default boolean isEmptyt(){
    return from().equals(until());
  }

  default Duration duration(){
    return Duration.between(from(), until());
  }

  default boolean contains(final Instant instant){
    return !instant.isBefore(from()) && instant.isBefore(until());
  }

  default boolean contains(final TimeSpan other){
    return contains(other.from()) && contains(other.until());
  }

  default Opt<TimeSpan> intersection(final TimeSpan other){
    final Instant from = max(from(), other.from());
    final Instant until = min(until(), other.until());
    return until.isBefore(from) ? Opt.empty() : Opt.of(create(from, until));
  }

  @Override
  default Class<TimeSpan> clazz() {
    return TimeSpan.class;
  }

  @Override
  default int compareWithOtherOfSameType(final TimeSpan o) {
    return COMPARATOR.compare(this, o);
  }

}
