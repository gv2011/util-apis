package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.Verify.verify;

import java.util.regex.Pattern;

import com.github.gv2011.util.StringUtils;
import com.github.gv2011.util.tstr.AbstractTypedString;

public final class FileExtension extends AbstractTypedString<FileExtension>{

  private static final Pattern PATTERN = Pattern.compile("\\w[\\-\\w\\.\\+]*");

  public static final FileExtension EMPTY = new FileExtension("");

  private final String extension;

  public static FileExtension parse(final String extension) {
    return extension.isEmpty() ? EMPTY : new FileExtension(extension);
  }

  private FileExtension(final String extension) {
    if(!extension.isEmpty()){
      verify(extension, StringUtils::isLowerCase);
      verify(extension, e->PATTERN.matcher(e).matches());
    }
    this.extension = extension;
  }

  @Override
  public FileExtension self() {
    return this;
  }

  @Override
  public Class<FileExtension> clazz() {
    return FileExtension.class;
  }

  @Override
  public String toString() {
    return extension;
  }

  public String appendTo(final String base){
    return extension.isEmpty() ? base : base+"."+extension;
  }

}
