package com.github.gv2011.util.beans;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation for enum fields.
 * Allows backwards compatible addition of new enum constants.
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Other {

}
