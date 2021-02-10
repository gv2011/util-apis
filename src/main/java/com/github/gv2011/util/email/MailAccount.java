package com.github.gv2011.util.email;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.sec.Domain;

public interface MailAccount extends Bean{
  
  Domain host();
  
  String user();
  
  String password();

}
