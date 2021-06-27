package com.github.gv2011.util.bytes;

import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.internal.DataTypeImp;

public interface DataTypeProvider {

  public static DataTypeProvider instance(){return DataTypeImp.DATA_TYPE_PROVIDER.get();}

  ISet<DataType> dataTypesForExtension(FileExtension extension);

  DataType dataTypeForExtension(FileExtension extension);

  ISet<DataType> knownDataTypes();

  Opt<FileExtension> preferredFileExtension(DataType dataType);

  ISortedSet<FileExtension> fileExtensions(DataType dataType);

}
