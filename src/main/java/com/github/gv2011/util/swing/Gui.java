package com.github.gv2011.util.swing;

import com.github.gv2011.util.AutoCloseableNt;

public interface Gui extends AutoCloseableNt{

  void waitUntilClosed();

  void setSize(int width, int height);

}
