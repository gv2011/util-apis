package com.github.gv2011.util.email;

import static com.github.gv2011.util.icol.ICollections.listOf;

import java.util.function.Consumer;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.bytes.DataTypes;

public interface MailProvider {

  AutoCloseableNt createMailListener(Consumer<Email> mailReceiver, MailAccount mailAccount);
  
  default Email asEmail(Bytes raw){
    return (Email) raw.typed(DataTypes.MESSAGE_RFC822);
  }

  ParsedEmail parse(Email opaque);
  
  default ParsedEmail sendEmail(EmailAddress recipient, String subject, String content, MailAccount mailAccount){
    return sendEmail(
      ( BeanUtils.beanBuilder(ParsedEmail.class)
        .set(ParsedEmail::to).to(listOf(recipient))
        .set(ParsedEmail::subject).to(subject)
        .set(ParsedEmail::plainText).to(content)
        .build()
      ),
      mailAccount
    );
  }

  ParsedEmail sendEmail(ParsedEmail email, MailAccount mailAccount);

}
