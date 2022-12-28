package com.github.gv2011.util.beans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.gv2011.util.tstr.TypedString.TypedStringParser;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ParserClass {

  Class<? extends TypedStringParser<?>> value();

}
