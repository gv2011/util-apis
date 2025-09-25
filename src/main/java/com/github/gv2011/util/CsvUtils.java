package com.github.gv2011.util;

import static com.github.gv2011.util.ServiceLoaderUtils.lazyServiceLoader;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Opt;

public final class CsvUtils {

  private CsvUtils(){staticClass();}

  private static final Constant<CsvEngine> ENGINE = lazyServiceLoader(CsvEngine.class);

  public static final CsvEngine csvEngine(){return ENGINE.get();}

  public static final <B> IList<B> read(final Bytes csvFile, final CsvFormat format, final Class<B> beanClass) {
    return csvEngine().read(csvFile, format, beanClass);
  }

  public static interface CsvEngine{
    <B> IList<B> read(Bytes csvFile, CsvFormat format, Class<B> beanClass);
  }

  public static interface CsvFormat{

    default Opt<String> tryGetColumnName(final String propertyName){
      return Opt.of(propertyName);
    }

    default Opt<String> tryGetValue(final String propertyName){
      return Opt.empty();
    }

    default Opt<String> recordNumberProperty(){
      return Opt.empty();
    }

  }

}
