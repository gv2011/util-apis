package com.github.gv2011.util.time;

import static com.github.gv2011.util.Verify.verify;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.gv2011.util.tstr.AbstractTypedString;

final class TimeSpanImp extends AbstractTypedString<TimeSpan> implements TimeSpan {

  private static final Pattern SYNTAX = Pattern.compile("\\(([0-9TZ.:-]+),([0-9TZ.:-]+)\\)");

  private final Instant from;
  private final Instant until;

  static final TimeSpan parse(final CharSequence chars) {
    final Matcher matcher = SYNTAX.matcher(chars);
    verify(matcher, Matcher::matches);
    return new TimeSpanImp(Instant.parse(matcher.group(1)), Instant.parse(matcher.group(2)));
  }

  TimeSpanImp(final Instant from, final Instant until) {
    this.from = from;
    this.until = until;
  }

  @Override
  public Instant from() {
    return from;
  }

  @Override
  public Instant until() {
    return until;
  }

  @Override
  public TimeSpan self() {
    return this;
  }

  @Override
  public String toString() {
    return "("+from+","+until+")";
  }

}
