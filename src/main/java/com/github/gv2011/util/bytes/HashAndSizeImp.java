package com.github.gv2011.util.bytes;

import com.github.gv2011.util.beans.AbstractBean;
import com.github.gv2011.util.beans.Constructor;
import static com.github.gv2011.util.beans.Constructor.Variant.*;

public final class HashAndSizeImp extends AbstractBean<HashAndSize> implements HashAndSize{

  private static final ClassCache CLASS_CACHE = AbstractBean.createClassCache(HashAndSize.class);

  private final Hash256 hash;
  private final long size;

  @Constructor(PARAMETER_NAMES)
  public HashAndSizeImp(final Hash256 hash, final Long size) {
    this.hash = hash;
    this.size = size;
  }

  @Override
  protected Class<HashAndSize> clazz() {
    return HashAndSize.class;
  }

  @Override
  protected HashAndSizeImp self() {
    return this;
  }

  @Override
  protected ClassCache classCache() {
    return CLASS_CACHE;
  }

  @Override
  public Hash256 hash() {
    return hash;
  }

  @Override
  public Long size() {
    return size;
  }

}
