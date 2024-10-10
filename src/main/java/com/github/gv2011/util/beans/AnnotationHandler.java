package com.github.gv2011.util.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.github.gv2011.util.beans.Constructor.Variant;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.tstr.TypedString;
import com.github.gv2011.util.tstr.TypedString.TypedStringParser;

public interface AnnotationHandler {

  <B> Opt<Class<? extends TypeResolver<B>>> typeResolver(Class<? extends B> clazz);

  Opt<Class<? extends TypeNameStrategy>> typeNameStrategy(Class<?> clazz);

  Opt<String> typeNameProperty(final Class<?> clazz);

  boolean annotatedAsBean(Class<?> clazz);

  boolean annotatedAsConstructor(Constructor<?> constructor);

  boolean declaredAsAbstract(Class<?> clazz);

  boolean isPolymorphicRoot(Class<?> i);

  Opt<String> defaultValue(Method m);

  Opt<String> fixedValue(Method m);

  ISet<Class<?>> subClasses(Class<?> clazz);

  Opt<String> typeName(Class<?> clazz);

  boolean annotatedAsComputed(Method m);

  Opt<Class<?>> getImplementingClass(final Class<?> clazz);

  Opt<Class<? extends Parser<?>>> getParser(final Class<?> clazz);

  Opt<Class<? extends Validator<?>>> getValidatorClass(Class<?> clazz);

  <S extends TypedString<S>> Opt<String> getDefaultValue(Class<S> clazz);

  boolean delegateConstructor(Constructor<?> constr);

  boolean propertiesConstructor(Constructor<?> constr);

  Variant getType(Constructor<?> constr);

  <T extends TypedString<T>> Opt<TypedStringParser<T>> getTypedStringParser(Class<T> typedStringClass);

}
