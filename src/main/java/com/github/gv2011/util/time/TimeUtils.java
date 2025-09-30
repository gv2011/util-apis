package com.github.gv2011.util.time;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.gv2011.util.Physics;
import com.github.gv2011.util.icol.Opt;

public class TimeUtils {

  public static final double HOUR = toSeconds(Duration.ofHours(1));

  public static final ZoneId UTC = ZoneId.of("UTC");

  public static final DateTimeFormatter DIN_1355_1_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY);

  private static final Pattern HOURS = Pattern.compile("(-?\\d+)(:([0-5]\\d)(:(([0-5]\\d)([,\\.](\\d+))?))?)?");
  private static final long NANOS_PER_SECOND = Duration.ofSeconds(1).toNanos();
  private static final double NANO = 1d / NANOS_PER_SECOND;

  private static final Pattern DD_MM_YYYY = Pattern.compile("(\\d{2})\\.(\\d{2})\\.(\\d{4})");



  private TimeUtils(){staticClass();}

  public static void await(final Instant instant){
    Clock.INSTANCE.get().await(instant);
  }

  public static void sleep(final Duration duration){
    Clock.INSTANCE.get().sleep(duration);
  }

  public static Duration parseHours(final String withColons) {
    final Matcher matcher = HOURS.matcher(withColons);
    verify(withColons, t->matcher.matches());
    final int hours = Integer.parseInt(matcher.group(1));
    final int minutes = Optional.ofNullable(matcher.group(3)).map(Integer::parseInt).orElse(0);
    final double seconds = matcher.group(5) == null
      ? 0d
      : Double.parseDouble(matcher.group(6))
    ;
    final Duration d =
      Duration.ofNanos((long)(seconds * NANOS_PER_SECOND))
      .plus(minutes, ChronoUnit.MINUTES)
      .plus(Math.abs(hours), ChronoUnit.HOURS)
    ;
    return hours>=0 ? d : d.negated();
  }

  public static double toSeconds(final Duration time) {
    double result = time.getSeconds();
    result += (time.getNano()) * NANO;
    return result;
  }

  public static String fileSafeFormat(final Instant instant) {
    return instant.toString().replace(':', '.');
  }

  public static String fileSafeInstant() {
    return fileSafeFormat(Instant.now());
  }

  public static boolean olderThan(final Temporal instant, final Duration duration) {
    return Duration.between(instant, Instant.now()).compareTo(duration) > 0;
  }

  public static final LocalDate fromDdMmYyyy(final String ddMmYyyy){
    final Matcher m = DD_MM_YYYY.matcher(ddMmYyyy);
    if(!m.matches()) throw new IllegalArgumentException();
    return LocalDate.parse(m.group(3)+"-"+m.group(2)+"-"+m.group(1));
  }

  public static final int week(final LocalDate date){
    return date.get(WeekFields.ISO.weekOfWeekBasedYear());
  }

  public static final Poller poller(final Duration interval, final Opt<Duration> timeout){
    return Clock.get().poller(interval, timeout);
  }

  public static final Instant earliest(final Instant i1, final Instant i2) {
    return i1.isBefore(i2) ? i1 : i2;
  }

  public static final Instant latest(final Instant i1, final Instant i2) {
    return i1.isAfter(i2) ? i1 : i2;
  }

  public static final Duration min(final Duration d1, final Duration d2) {
    return d1.compareTo(d2)<0 ? d1 : d2;
  }

  public static final Duration max(final Duration d1, final Duration d2) {
    return d1.compareTo(d2)>0 ? d1 : d2;
  }

  public static final boolean isPositive(final Duration duration){
    return !duration.isNegative() && !duration.isZero();
  }

  public static final String approx(final Duration d) {
    return d.abs().toDays()>3 ? d.toDays()+" days" : d.toString();
  }

  public static Duration toDuration(final double seconds) {
    final long fullSeconds = (long) seconds;
    final long nanos = (long)((seconds - ((double)fullSeconds)) / Physics.nano);
    final Duration part1 = Duration.ofSeconds(fullSeconds);
    try {
      return part1.plusNanos(nanos);
    } catch (final ArithmeticException e) {
      try {
        return part1.plusMillis(nanos/1000000L);
      } catch (final ArithmeticException es) {
        return part1;
      }
    }
  }

  public static double duration(final Instant t1, final Instant t2) {
    return toSeconds(Duration.between(t1, t2));
  }

}
