package com.github.gv2011.util.filewatch;

import java.nio.file.Path;
import java.util.function.Function;

import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.bytes.Hash256;

public interface FileWatchService {

  Bytes readFile(Path file, Function<Bytes,Boolean> changedCallback);

  void watch(Path file, Hash256 current, Function<Bytes,Boolean> changedCallback);

}
