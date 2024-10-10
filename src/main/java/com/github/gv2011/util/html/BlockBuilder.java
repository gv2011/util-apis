package com.github.gv2011.util.html;

import java.net.URI;

public interface BlockBuilder{

  HtmlFactory factory();

  BlockBuilder close();

  BlockBuilder addText(String text);

  BlockBuilder addBlock();

  BlockBuilder addBlock(BlockType blockType);

  FormBuilder addForm();

  BlockBuilder addAnchor(String text, URI url);

}
