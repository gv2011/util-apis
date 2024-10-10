package com.github.gv2011.util.html;

public interface HtmlFactory {

  HtmlBuilder newBuilder();

  BlockType blockType(String name);

  BlockType table();

  BlockType tr();

  BlockType td();

}
