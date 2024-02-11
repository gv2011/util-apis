package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.io.InputStream;
import java.io.OutputStream;

import com.github.gv2011.util.bytes.Hash;
import com.github.gv2011.util.bytes.Hash256;
import com.github.gv2011.util.bytes.HashAndSize;
import com.github.gv2011.util.bytes.HashFactory;
import com.github.gv2011.util.bytes.HashFactory.HashBuilder;
import com.github.gv2011.util.ex.ThrowingSupplier;

public final class HashUtils {

  private HashUtils(){staticClass();}

  public static Hash hash(final HashAlgorithm algorithm, final ThrowingSupplier<InputStream> input){
    return HashFactory.INSTANCE.get().hash(algorithm, input);
  }

  public static Hash256 hash256(final ThrowingSupplier<InputStream> input){
    return HashFactory.INSTANCE.get().hash256(input);
  }

  public static HashAndSize hash256(final ThrowingSupplier<InputStream> input, final OutputStream out){
    return HashFactory.INSTANCE.get().hash256(input, out);
  }

  public static HashBuilder hashBuilder() {
    return HashFactory.INSTANCE.get().hashBuilder();
  }
}
