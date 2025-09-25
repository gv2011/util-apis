package com.github.gv2011.util.swing;

import java.nio.file.Path;

import com.github.gv2011.util.icol.Opt;

public interface SwingFactory {

  GuiBuilder guiBuilder();

  Opt<Path> selectFile(Path initialDirectory, String title);

}
