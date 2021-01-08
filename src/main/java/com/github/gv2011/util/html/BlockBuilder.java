package com.github.gv2011.util.html;

import java.net.URI;

public interface BlockBuilder {

  BlockBuilder close();

  BlockType blockType(String name);

  BlockBuilder setBlockType(BlockType blockType);

  BlockBuilder addText(String text);

  BlockBuilder addBlock();

  FormBuilder addForm();

  BlockBuilder addAnchor(String text, URI url);

}
