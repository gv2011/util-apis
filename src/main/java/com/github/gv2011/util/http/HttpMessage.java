package com.github.gv2011.util.http;

import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.icol.Opt;

public interface HttpMessage{

  Opt<TypedBytes> entity();

}
