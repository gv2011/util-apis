package com.github.gv2011.util.internal;

import static com.github.gv2011.util.StringUtils.toLowerCase;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.beans.Constructor.Variant.PARAMETER_NAMES;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.emptySortedMap;
import static com.github.gv2011.util.icol.ICollections.toISortedMap;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Enumeration;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.StringUtils;
import com.github.gv2011.util.beans.BeanHashCode;
import com.github.gv2011.util.beans.Computed;
import com.github.gv2011.util.beans.Constructor;
import com.github.gv2011.util.beans.ExtendedBeanBuilder;
import com.github.gv2011.util.beans.Parser;
import com.github.gv2011.util.beans.Validator;
import com.github.gv2011.util.bytes.DataType;
import com.github.gv2011.util.bytes.DataTypeProvider;
import com.github.gv2011.util.bytes.FileExtension;
import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;

import jakarta.activation.MimeType;
import jakarta.activation.MimeTypeParameterList;

public final class DataTypeImp implements DataType {

  public static final Constant<DataTypeProvider> DATA_TYPE_PROVIDER =
    RecursiveServiceLoader.lazyService(DataTypeProvider.class)
  ;

  private static final ToIntFunction<DataType> HASH_FUNCTION = BeanHashCode.createHashCodeFunction(
    DataType.class, DataType::primaryType, DataType::subType, DataType::parameters
  );

//  private static final ISet<UChar> ALLOWED_CHARACTERS = UChars.uChar(' ').range(' ');//"()<>@,;:/[]?=\\\"";

  private final Constant<Opt<FileExtension>> preferredFileExtension = Constants.cachedConstant(
    ()->DATA_TYPE_PROVIDER.get().preferredFileExtension(this)
  );

  private final Constant<ISortedSet<FileExtension>> fileExtensions = Constants.cachedConstant(
    ()->DATA_TYPE_PROVIDER.get().fileExtensions(this)
  );

  private final String primaryType;
  private final String subType;
  private final ISortedMap<String, String> parameters;
  private final Constant<Integer> hash = Constants.cachedConstant(()->HASH_FUNCTION.applyAsInt(this));

  @Constructor(PARAMETER_NAMES)
  public DataTypeImp(final String primaryType, final String subType, final ISortedMap<String, String> parameters) {
    this.primaryType = primaryType;
    this.subType = subType;
    this.parameters = parameters;
  }

  @Override
  public String primaryType() {
    return primaryType;
  }

  @Override
  public String subType() {
    return subType;
  }

  @Override
  public ISortedMap<String, String> parameters() {
    return parameters;
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
    return parameters().tryGet(DataType.CHARSET_PARAMETER_NAME).map(Charset::forName);
  }

  @Override
  public DataType withCharset(final Charset charset) {
    return new DataTypeImp(
      primaryType,
      subType,
      ( ICollections.<String, String>sortedMapBuilder()
        .putAll(parameters())
        .put(CHARSET_PARAMETER_NAME, charset.name())
        .build()
      )
    );
  }

  @Override
  @Computed
  public DataType baseType() {
    return parameters().isEmpty()
      ? this
      : new DataTypeImp(primaryType, subType, emptySortedMap())
    ;
  }

  @Override
  public int hashCode() {
    return hash.get();
  }

  @Override
  public boolean equals(final Object obj) {
    return BeanUtils.equals(this, obj, DataType.class, DataType::primaryType, DataType::subType, DataType::parameters);
  }

  @Override
  public String toString() {
    return primaryType()+"/"+subType() + parametersToString();
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

  public static DataType parse(final String encoded) {
    final MimeType mimeType = call(()->new MimeType(encoded));
    return new DataTypeImp(
      toLowerCase(mimeType.getPrimaryType()),
      toLowerCase(mimeType.getSubType()),
      ( Collections.list((Enumeration<?>)mimeType.getParameters().getNames())
        .stream().map(n->
        toLowerCase((String)n)
        )
        .collect(toISortedMap(
          name->name,
          name->{
            final String parameterValue = mimeType.getParameter(name);
            return name.equals(CHARSET_PARAMETER_NAME)
              ? call(()->canonicalCharsetName(parameterValue), ()->format("{} cannot be parsed as DataType.", encoded))
              : parameterValue
            ;
          }
        ))
      )
    );
  }

  private static String canonicalCharsetName(final String charset) {
    final String name = Charset.forName(charset).name();
    verifyEqual(toLowerCase(charset), toLowerCase(name));
    return name;
  }




  public static final class DataTypeParser implements Parser<DataType>{
    @Override
    public DataType parse(final String encoded, final ExtendedBeanBuilder<DataType> builder) {
      return DataTypeImp.parse(encoded);
    }
  }

  public static final class DataTypeValidator implements Validator<DataType>{
    @Override
    public String invalidMessage(final DataType dataType) {
      final String encoded = dataType.toString();
      final DataType parsed;
      try {
        parsed = parse(encoded);
      } catch (final Exception e) {
        return format("Invalid: {} ({}).", encoded, e.getMessage());
      }
      return dataType.equals(parsed) ? Validator.VALID : "Parse mismatch.";
    }
  }

}
