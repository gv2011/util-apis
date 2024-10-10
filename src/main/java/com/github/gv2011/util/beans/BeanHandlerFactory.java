package com.github.gv2011.util.beans;

import static com.github.gv2011.util.icol.ICollections.empty;

import com.github.gv2011.util.icol.Opt;

public interface BeanHandlerFactory {

  default <T> Opt<BeanHandler<T>> createBeanHandler(final Class<T> beanClass) {
    return empty();
  }

}
