package com.github.gv2011.util.bytes;

import java.nio.charset.Charset;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.beans.Computed;
import com.github.gv2011.util.beans.Final;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.internal.DataTypeImp;
import com.github.gv2011.util.internal.DataTypeImp.DataTypeParser;
import com.github.gv2011.util.internal.DataTypeImp.DataTypeValidator;


/**
 * A Multipurpose Internet Mail Extension (MIME) type, as defined in RFC 2045
 * and 2046.
 */
@Final(implementation=DataTypeImp.class, parser=DataTypeParser.class, validator=DataTypeValidator.class)
public interface DataType extends Bean{

  public static final String CHARSET_PARAMETER_NAME = "charset";


  public static DataType parse(final String dataType){
    return DataTypeImp.parse(dataType);
  }

  String primaryType();

  String subType();

  ISortedMap<String,String> parameters();

  @Computed
  Opt<FileExtension> preferredFileExtension();

  @Computed
  ISortedSet<FileExtension> fileExtensions();

  @Computed
  Opt<Charset> charset();

  /**
   * @return a String representation without the parameter list
   */
  @Computed
  DataType baseType();

  DataType withCharset(Charset charset);

}
