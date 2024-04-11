package com.github.gv2011.util.time;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;

public enum Hour implements TemporalAccessor, TemporalAdjuster{

  H00( 0), H01( 1), H02( 2), H2A(2), H2B( 2), H03( 3), H04( 4), H05( 5),
  H06( 6), H07( 7), H08( 8),                  H09( 9), H10(10), H11(11),
  H12(12), H13(13), H14(14),                  H15(15), H16(16), H17(17),
  H18(18), H19(19), H20(20),                  H21(21), H22(22), H23(23),
  H24( 0);

  public static final TemporalField FULL_HOUR = new FullHourField();

  private final int wallHour24;

  Hour(final int wallHour24) {
    this.wallHour24 = wallHour24;
  }

  @Override
  public String toString(){
    return name().substring(1, 3);
  }

  /**
   * 0 .. 23
   */
  public int wallHour24(){
    return wallHour24;
  }

  /**
   * 1 .. 12
   */
  public int wallHour12(){
    return wallHour24>12 ? wallHour24-12 : wallHour24==0 ? 12 : wallHour24;
  }



  @Override
  public Temporal adjustInto(final Temporal temporal) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isSupported(final TemporalField field) {
    return field==FULL_HOUR;
  }

  @Override
  public long getLong(final TemporalField field) {
    // TODO Auto-generated method stub
    return 0;
  }


  private static final class FullHourField implements TemporalField{

    @Override
    public TemporalUnit getBaseUnit() {
      return ChronoUnit.HOURS;
    }

    @Override
    public TemporalUnit getRangeUnit() {
      return ChronoUnit.DAYS;
    }

    @Override
    public ValueRange range() {
      return ValueRange.of(0, 23, 25);
    }

    @Override
    public boolean isDateBased() {
      return true;
    }

    @Override
    public boolean isTimeBased() {
      return true;
    }

    @Override
    public boolean isSupportedBy(final TemporalAccessor temporal) {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public ValueRange rangeRefinedBy(final TemporalAccessor temporal) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public long getFrom(final TemporalAccessor temporal) {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public <R extends Temporal> R adjustInto(final R temporal, final long newValue) {
      // TODO Auto-generated method stub
      return null;
    }
  }

}
