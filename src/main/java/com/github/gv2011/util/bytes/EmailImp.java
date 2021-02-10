package com.github.gv2011.util.bytes;

import com.github.gv2011.util.email.Email;

final class EmailImp extends AbstractTypedBytes implements Email{

  private final Bytes content;
  
  EmailImp(Bytes content) {
    this.content = content;
  }

  @Override
  public Bytes content() {
    return content;
  }

  @Override
  public DataType dataType() {
    return DataTypes.MESSAGE_RFC822;
  }

}
