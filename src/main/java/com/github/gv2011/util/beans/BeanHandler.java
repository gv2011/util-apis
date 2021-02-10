package com.github.gv2011.util.beans;

import static com.github.gv2011.util.Verify.verify;

import java.util.function.UnaryOperator;

public interface BeanHandler<T> {
  
  default boolean canParse(){
    return false;
  }
  
  default T parse(String encoded){
    verify(!canParse(), ()->"Implementation missing.");
    throw new UnsupportedOperationException();
  }
  
  default T wrapBean(T core, UnaryOperator<T> annotatedWrapper){
    return annotatedWrapper.apply(core);
  }

}
