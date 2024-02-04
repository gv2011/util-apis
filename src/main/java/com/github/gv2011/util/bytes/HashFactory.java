package com.github.gv2011.util.bytes;

import java.io.InputStream;
import java.io.OutputStream;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.HashAlgorithm;
import com.github.gv2011.util.ex.ThrowingSupplier;

public interface HashFactory {

  public static final Constant<HashFactory> INSTANCE = Constants.cachedConstant(HashFactoryImp::new);

  Hash hash(final HashAlgorithm algorithm, final ThrowingSupplier<InputStream> input);

  default Hash256 hash256(final ThrowingSupplier<InputStream> in){
    return hash256(in, OutputStream.nullOutputStream()).hash();
  }

  HashAndSize hash256(final ThrowingSupplier<InputStream> in, OutputStream out);

  HashBuilder hashBuilder();

  static interface HashBuilder{
    OutputStream outputStream();
    HashAndSize build();
  }

}
