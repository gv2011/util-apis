package com.github.gv2011.util.beans;

import com.github.gv2011.util.icol.Opt;

public interface BeanHandlerFactory {
  
  <T> Opt<BeanHandler<T>> createBeanHandler(Class<T> beanClass);

}
