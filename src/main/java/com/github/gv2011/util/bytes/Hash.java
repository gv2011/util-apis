package com.github.gv2011.util.bytes;

import com.github.gv2011.util.HashAlgorithm;
import com.github.gv2011.util.beans.Elementary;

public interface Hash extends TypedBytes, Elementary{

  HashAlgorithm algorithm();

}
