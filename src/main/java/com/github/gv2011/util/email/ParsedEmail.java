package com.github.gv2011.util.email;

import java.time.Instant;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Opt;

public interface ParsedEmail extends Bean{
  
  IList<EmailAddress> from();
  
  IList<EmailAddress> replyTo();
  
  IList<EmailAddress> to();
  
  IList<EmailAddress> cc();
  
  IList<EmailAddress> bcc();
  
  String subject();
  
  Opt<Instant> sentDate();
  
  Opt<Instant> receivedDate();

  String plainText();

}
