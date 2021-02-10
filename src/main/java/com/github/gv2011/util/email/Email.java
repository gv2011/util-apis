package com.github.gv2011.util.email;

import com.github.gv2011.util.beans.FixedValue;
import com.github.gv2011.util.bytes.DataType;
import com.github.gv2011.util.bytes.DataTypes;
import com.github.gv2011.util.bytes.TypedBytes;

public interface Email extends TypedBytes{
  
  @Override
  @FixedValue(value = DataTypes.MESSAGE_RFC822_STR)
  DataType dataType();

}
