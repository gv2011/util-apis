package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.bytes.DataTypes.APPLICATION_OCTET_STREAM;
import static com.github.gv2011.util.bytes.DataTypes.TEXT_PLAIN;
import static com.github.gv2011.util.icol.ICollections.emptySortedSet;
import static com.github.gv2011.util.icol.ICollections.sortedSetOf;
import static com.github.gv2011.util.icol.ICollections.toIList;
import static com.github.gv2011.util.icol.ICollections.toISet;

import java.util.Arrays;

import com.github.gv2011.util.HashAlgorithm;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.icol.Opt;

public final class DefaultDataTypeProvider implements DataTypeProvider{

  private static final FileExtension TXT = new FileExtension("txt");
  
  @Override
  public ISet<DataType> knownDataTypes() {
    return
      XStream.of(TEXT_PLAIN, APPLICATION_OCTET_STREAM)
      .concat(Arrays.stream(HashAlgorithm.values()).map(HashAlgorithm::getDataType))
      .collect(toISet())
    ;
  }

  @Override
  public Opt<FileExtension> preferredFileExtension(final DataType dataType) {
    if(dataType.baseType().equals(TEXT_PLAIN)) return Opt.of(TXT);
    else return Opt.empty();
  }

  @Override
  public ISortedSet<FileExtension> fileExtensions(final DataType dataType) {
    if(dataType.baseType().equals(TEXT_PLAIN)) return sortedSetOf(TXT);
    else return emptySortedSet();
  }

  @Override
  public ISet<DataType> dataTypesForExtension(final FileExtension extension) {
    return knownDataTypes().stream().filter(dt->dt.fileExtensions().contains(extension)).collect(toISet());
  }

  @Override
  public DataType dataTypeForExtension(final FileExtension extension) {
    final IList<DataType> candidates = knownDataTypes().stream()
      .filter(dt->dt.preferredFileExtension().map(e->e.equals(extension)).orElse(false))
      .collect(toIList())
    ;
    return candidates.size()==1 ? candidates.single() : APPLICATION_OCTET_STREAM;
  }

}
