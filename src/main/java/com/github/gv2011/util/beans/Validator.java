package com.github.gv2011.util.beans;

import static com.github.gv2011.util.ex.Exceptions.format;

public interface Validator<P> {

  public static final String VALID = "";

  default String invalidMessage(final P argument) {
    return isValid(argument) ? VALID : format("Invalid: \"{}\".", argument);
  }

  default boolean isValid(final P argument) {
    return true;
  }

}
