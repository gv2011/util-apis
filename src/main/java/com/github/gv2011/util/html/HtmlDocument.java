package com.github.gv2011.util.html;

import java.io.OutputStream;

import org.w3c.dom.Document;

import com.github.gv2011.util.bytes.TypedBytes;

public interface HtmlDocument {

  Document dom();

  String title();

  long write(OutputStream out);
  
  TypedBytes asEntity();

}
