package com.github.gv2011.util.bytes;

import java.io.InputStream;
import java.io.OutputStream;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.HashAlgorithm;
import com.github.gv2011.util.ex.ThrowingSupplier;

public interface HashFactory {

  public static final Constant<HashFactory> INSTANCE = Constants.cachedConstant(HashFactoryImp::new);

  TypedBytes hash(final HashAlgorithm algorithm, final ThrowingSupplier<InputStream> input);

  HashAndSize hash256(final ThrowingSupplier<InputStream> in, OutputStream out);

  HashBuilder hashBuilder();

  static interface HashBuilder{
    OutputStream outputStream();
    HashAndSize build();
  }

}
