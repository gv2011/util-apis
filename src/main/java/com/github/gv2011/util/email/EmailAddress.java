package com.github.gv2011.util.email;

import com.github.gv2011.util.tstr.TypedString;

public interface EmailAddress extends TypedString<EmailAddress>{
  
  public static EmailAddress parse(String emailAddress){
    return TypedString.create(EmailAddress.class, emailAddress);
  }
  
}
