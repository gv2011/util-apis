package com.github.gv2011.util.log;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.util.function.Function;

import org.slf4j.Logger;

public final class LogUtils {

    private LogUtils(){staticClass();}

    public static <V> V log(final V value, final Logger logger, final String messagePattern){
      if(logger.isInfoEnabled()) logger.info(messagePattern, value);
      return value;
    }

    public static <V> V log(final V value, final Logger logger, final Function<V,String> message){
      if(logger.isInfoEnabled()) logger.info(message.apply(value));
      return value;
    }
}
