package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.bytes.DataType.CHARSET_PARAMETER_NAME;
import static com.github.gv2011.util.bytes.DataType.parse;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;

public final class DataTypes {

  private DataTypes(){staticClass();}

  public static final String APPLICATION = "application";
  public static final String OCTET_STREAM = "octet-stream";
  public static final DataType APPLICATION_OCTET_STREAM = parse(APPLICATION+"/"+OCTET_STREAM);

  public static final String TEXT = "text";
  public static final String PLAIN = "plain";
  public static final DataType TEXT_PLAIN = parse(TEXT+"/"+PLAIN);
  public static final DataType TEXT_PLAIN_UTF_8 = parse(TEXT_PLAIN+";"+CHARSET_PARAMETER_NAME+"="+UTF_8.name());
  
  public static final DataType XHTML = parse(APPLICATION+"/xhtml+xml;"+CHARSET_PARAMETER_NAME+"="+UTF_8.name());

  public ISet<DataType> getAllKnownDataTypes() {
    return
      RecursiveServiceLoader.services(DataTypeProvider.class).stream()
      .flatMap(p->p.knownDataTypes().stream())
      .collect(toISet())
    ;
  }

}
