package com.github.gv2011.util.swing;

import javax.swing.JComponent;

public interface GuiBuilder {

  GuiBuilder add(JComponent component, boolean newLine);

  GuiBuilder add(JComponent component, HTab rightTab);

  GuiBuilder addSoftGap(boolean newLine);

  GuiBuilder addHorizontalHardGap(int size, boolean newLine);
  GuiBuilder addVerticalHardGap  (int size, boolean newLine);

  Position position();

  GuiBuilder setSize(int width, int height);


  Gui build();

}
