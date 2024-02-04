package com.github.gv2011.util.log;

import java.net.URI;
import java.nio.file.Path;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.icol.Opt;


public interface LogAdapter extends AutoCloseableNt{

  void ensureInitialized();

  Opt<Path> tryGetLogFileDirectory();

  Opt<URI> tryGetLogConfiguration();

}
