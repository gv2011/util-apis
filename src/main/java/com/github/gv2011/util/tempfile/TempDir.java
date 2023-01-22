package com.github.gv2011.util.tempfile;

import java.nio.file.Path;

import com.github.gv2011.util.AutoCloseableNt;

public interface TempDir extends AutoCloseableNt{

  Path path();

}
