package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.bytes.DataType.CHARSET_PARAMETER_NAME;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static com.github.gv2011.util.internal.DataTypeImp.parse;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;

public final class DataTypes {

  private DataTypes(){staticClass();}

  public static final String APPLICATION = "application";
  public static final String OCTET_STREAM = "octet-stream";
  public static final String JSON = "json";
  public static final DataType APPLICATION_OCTET_STREAM = parse(APPLICATION+"/"+OCTET_STREAM);
  public static final DataType APPLICATION_JSON = parse(APPLICATION+"/"+JSON);

  public static final String TEXT = "text";
  public static final String PLAIN = "plain";
  public static final DataType TEXT_PLAIN = parse(TEXT+"/"+PLAIN);
  public static final DataType TEXT_PLAIN_UTF_8 = parse(TEXT_PLAIN+";"+CHARSET_PARAMETER_NAME+"="+UTF_8.name());

  public static final String XML = "xml";
  public static final DataType XHTML = parse(APPLICATION+"/xhtml+"+XML+";"+CHARSET_PARAMETER_NAME+"="+UTF_8.name());

  public static final String MESSAGE = "message";
  public static final String RFC822 = "rfc822";
  public static final String MESSAGE_RFC822_STR = MESSAGE+"/"+RFC822;
  public static final DataType MESSAGE_RFC822 = parse(MESSAGE_RFC822_STR);

  public static final String MULTIPART = "multipart";
  public static final String MIXED = "mixed";
  public static final DataType MULTIPART_MIXED = parse(MULTIPART+"/"+MIXED);
  public static final String ALTERNATIVE = "alternative";
  public static final DataType MULTIPART_ALTERNATIVE = parse(MULTIPART+"/"+ALTERNATIVE);

  public static final String IMAGE = "image";

  public static final DataType SVG = parse(IMAGE+"/svg+"+XML);

//  [image/svg+xml]


  public ISet<DataType> getAllKnownDataTypes() {
    return
      RecursiveServiceLoader.services(DataTypeProvider.class).stream()
      .flatMap(p->p.knownDataTypes().stream())
      .collect(toISet())
    ;
  }

}
