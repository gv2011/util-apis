package com.github.gv2011.util.beans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.gv2011.util.icol.Nothing;

/**
 * Instances that directly implement the annotated interface must not be created.
 * The annotated interface serves only as superinterface for other interfaces.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AbstractIntermediate {

    Class<?>[] subClasses() default Nothing.class;

    @SuppressWarnings("rawtypes")
    Class<? extends TypeResolver> typeResolver() default TypeResolver.class;

    Class<? extends TypeNameStrategy> typeNameStrategy() default TypeNameStrategy.class;
}
