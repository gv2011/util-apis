package com.github.gv2011.util.sec;

import java.security.KeyStore;

import com.github.gv2011.util.bytes.TypedBytes;

public interface SimpleKeyStore extends DestroyingCloseable{
  
  Domain domain();
  
  KeyStore asKeyStore();
  
  TypedBytes asBytes();

}
