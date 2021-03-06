package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.StringUtils.toLowerCase;
import static com.github.gv2011.util.Verify.verifyEqual;
/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.toISortedMap;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Enumeration;
import java.util.stream.Collectors;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.StringUtils;
import com.github.gv2011.util.beans.Computed;
import com.github.gv2011.util.beans.ExtendedBeanBuilder;
import com.github.gv2011.util.beans.Parser;
import com.github.gv2011.util.beans.Validator;
import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;

import jakarta.activation.MimeType;
import jakarta.activation.MimeTypeParameterList;

public final class DataTypeImp implements DataType {

  static final Constant<DataTypeProvider> DATA_TYPE_PROVIDER =
    RecursiveServiceLoader.lazyService(DataTypeProvider.class)
  ;

//  private static final ISet<UChar> ALLOWED_CHARACTERS = UChars.uChar(' ').range(' ');//"()<>@,;:/[]?=\\\"";


  private final DataType core;

  private final Constant<Opt<FileExtension>> preferredFileExtension = Constants.cachedConstant(
    ()->DATA_TYPE_PROVIDER.get().preferredFileExtension(this)
  );

  private final Constant<ISortedSet<FileExtension>> fileExtensions = Constants.cachedConstant(
    ()->DATA_TYPE_PROVIDER.get().fileExtensions(this)
  );

  public DataTypeImp(final DataType core) {
    this.core = core;
  }

  @Override
  public String primaryType() {
    return core.primaryType();
  }

  @Override
  public String subType() {
    return core.subType();
  }

  @Override
  public ISortedMap<String, String> parameters() {
    return core.parameters();
  }

  @Override
  @Computed
  public Opt<FileExtension> preferredFileExtension() {
    return preferredFileExtension.get();
  }

  @Override
  @Computed
  public ISortedSet<FileExtension> fileExtensions() {
    return fileExtensions.get();
  }

  @Override
  @Computed
  public Opt<Charset> charset() {
    return core.parameters().tryGet(DataType.CHARSET_PARAMETER_NAME).map(Charset::forName);
  }
  
  @Override
  public DataType withCharset(Charset charset) {
    return BeanUtils.beanBuilder(DataType.class)
      .set(DataType::primaryType).to(core.primaryType())
      .set(DataType::subType).to(core.subType())
      .set(DataType::parameters).to(
        ICollections.<String, String>sortedMapBuilder()
        .putAll(core.parameters())
        .put(CHARSET_PARAMETER_NAME, charset.name())
        .build()
      )
      .build()
    ;
  }

  @Override
  @Computed
  public DataType baseType() {
    return core.parameters().isEmpty()
      ? this
      :( BeanUtils.beanBuilder(DataType.class)
        .set(DataType::primaryType).to(core.primaryType())
        .set(DataType::subType).to(core.subType())
        .build()
      )
    ;
  }

  @Override
  public int hashCode() {
    return core.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    return core.equals(obj);
  }

  @Override
  public String toString() {
    return core.primaryType()+"/"+core.subType() + parametersToString();
  }

  private String parametersToString() {
    return
      parameters().entrySet().stream()
      .map(e -> "; " + e.getKey() + "=" + quote(e.getValue()))
      .collect(Collectors.joining())
    ;
  }

  private static String quote(final String value) {
    final MimeTypeParameterList mtpl = new MimeTypeParameterList();
    mtpl.set("k", value);
    return StringUtils.removePrefix(mtpl.toString(),"; k=");
  }

  private static DataType parse(final String encoded, final ExtendedBeanBuilder<DataType> builder) {
    final MimeType mimeType = call(()->new MimeType(encoded));
    builder
      .set(DataType::primaryType).to(mimeType.getPrimaryType())
      .set(DataType::subType).to(mimeType.getSubType())
      .set(DataType::parameters)
      .to(
        Collections.list((Enumeration<?>)mimeType.getParameters().getNames())
        .stream().map(n->toLowerCase((String)n))
        .collect(toISortedMap(
          name->name,
          name->(name.equals(CHARSET_PARAMETER_NAME)
            ? canonicalCharsetName(mimeType.getParameter(CHARSET_PARAMETER_NAME))
            : mimeType.getParameter(name)
          )
        ))
      )
    ;
    return builder.buildUnvalidated();
  }

  private static String canonicalCharsetName(final String charset) {
    final String name = Charset.forName(charset).name();
    verifyEqual(toLowerCase(charset), toLowerCase(name));
    return name;
  }

//  private void parse2(final String rawdata) throws MimeTypeParseException {
//    final int slashIndex = rawdata.indexOf('/');
//    final int semIndex = rawdata.indexOf(';');
//    String primaryType;
//    String subType;
//    MimeTypeParameterList parameters;
//    if ((slashIndex < 0) && (semIndex < 0)) {
//        //    neither character is present, so treat it
//        //    as an error
//        throw new MimeTypeParseException("Unable to find a sub type.");
//    } else if ((slashIndex < 0) && (semIndex >= 0)) {
//        //    we have a ';' (and therefore a parameter list),
//        //    but no '/' indicating a sub type is present
//        throw new MimeTypeParseException("Unable to find a sub type.");
//    } else if ((slashIndex >= 0) && (semIndex < 0)) {
//        //    we have a primary and sub type but no parameter list
//        primaryType = rawdata.substring(0, slashIndex).trim().
//        toLowerCase(Locale.ENGLISH);
//        subType = rawdata.substring(slashIndex + 1).trim().
//        toLowerCase(Locale.ENGLISH);
//        parameters = new MimeTypeParameterList();
//    } else if (slashIndex < semIndex) {
//        //    we have all three items in the proper sequence
//        primaryType = rawdata.substring(0, slashIndex).trim().
//        toLowerCase(Locale.ENGLISH);
//        subType = rawdata.substring(slashIndex + 1, semIndex).trim().
//        toLowerCase(Locale.ENGLISH);
//        parameters = new MimeTypeParameterList(rawdata.substring(semIndex));
//    } else {
//        // we have a ';' lexically before a '/' which means we
//  // have a primary type and a parameter list but no sub type
//        throw new MimeTypeParseException("Unable to find a sub type.");
//    }
//
//    //    now validate the primary and sub types
//
//    //    check to see if primary is valid
//    if (!isValidToken(primaryType))
//        throw new MimeTypeParseException("Primary type is invalid.");
//
//    //    check to see if sub is valid
//    if (!isValidToken(subType))
//        throw new MimeTypeParseException("Sub type is invalid.");
//}

//  private boolean isValidType(final String s) {
//    UStr.class
//      final int len = s.length();
//      if (len > 0) {
//          for (int i = 0; i < len; ++i) {
//              final char c = s.charAt(i);
//              if (!isTokenChar(c)) {
//                  return false;
//              }
//          }
//          return true;
//      } else {
//          return false;
//      }
//  }

//  private static boolean isTokenChar(final char c) {
//    return ((c > 040) && (c < 0177)) && (TSPECIALS.indexOf(c) < 0);
//}


  public static final class DataTypeParser implements Parser<DataType>{
    @Override
    public DataType parse(final String encoded, final ExtendedBeanBuilder<DataType> builder) {
      return DataTypeImp.parse(encoded, builder);
    }
  }

  public static final class DataTypeValidator implements Validator<DataType>{
    @Override
    public String invalidMessage(final DataType dataType) {
      final String encoded = dataType.toString();
      final DataType parsed;
      try {
        parsed = parse(
          encoded,
          BeanUtils.typeRegistry().beanType(DataType.class).createBuilder()
        );
      } catch (final Exception e) {
        return format("Invalid: {} ({}).", encoded, e.getMessage());
      }
      return dataType.equals(parsed) ? Validator.VALID : "Parse mismatch.";
    }
  }

}
