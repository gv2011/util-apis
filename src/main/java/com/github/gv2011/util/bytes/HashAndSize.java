package com.github.gv2011.util.bytes;

import com.github.gv2011.util.beans.Bean;

public interface HashAndSize extends Bean{

  Hash256 hash();
  
  Long size();
  
}
