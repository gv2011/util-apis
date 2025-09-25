package com.github.gv2011.util.sec;


import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.tstr.TypedString;

public interface UnixSha512CryptHash extends TypedString<UnixSha512CryptHash> {

  boolean verify(String password);

  boolean verify(Bytes password);
}
