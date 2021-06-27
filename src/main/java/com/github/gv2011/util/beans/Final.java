package com.github.gv2011.util.beans;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.gv2011.util.icol.Nothing;


@Retention(RUNTIME)
@Target(TYPE)
public @interface Final {

  Class<?> implementation() default Nothing.class;

  Class<? extends Parser<?>> parser() default NoopParser.class;

  Class<? extends Validator<?>> validator() default NoopValidator.class;

  static interface NoopParser extends Parser<Object>{}
  static interface NoopValidator extends Validator<Object>{}

}
