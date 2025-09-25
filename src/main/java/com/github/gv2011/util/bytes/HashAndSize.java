package com.github.gv2011.util.bytes;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.beans.Final;

@Final(implementation=HashAndSizeImp.class)
public interface HashAndSize extends Bean{

  Hash256 hash();

  Long size();

  public static HashAndSize create(final Hash256 hash, final Long size){
    return new HashAndSizeImp(hash, size);
  }

}
