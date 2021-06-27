package com.github.gv2011.util.beans;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(CONSTRUCTOR)
public @interface Constructor {

  public Variant value() default Variant.DELEGATE;

  public static enum Variant{
    DELEGATE,
    PARAMETER_NAMES,
    ALPHABETIC
  }


}
