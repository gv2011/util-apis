package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.io.InputStream;

import com.github.gv2011.util.bytes.HashFactory;
import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.bytes.HashFactory.HashBuilder;
import com.github.gv2011.util.ex.ThrowingSupplier;

public final class HashUtils {

  private HashUtils(){staticClass();}

  public static TypedBytes hash(final HashAlgorithm algorithm, final ThrowingSupplier<InputStream> input){
    return HashFactory.INSTANCE.get().hash(algorithm, input);
  }

  public static HashBuilder hashBuilder() {
    return HashFactory.INSTANCE.get().hashBuilder();
  }

}
