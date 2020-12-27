package com.github.gv2011.util.m2t;

import java.nio.file.Path;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.icol.ISortedSet;

public interface M2t extends AutoCloseableNt{

  Path resolve(ArtifactRef artifact);

  /**
   * @return includes main artifact
   */
  ISortedSet<Path> getClasspath(ArtifactRef artifact, Scope scope);

  /**
   * @return does not include main artifact
   */
  ISortedSet<Path> getClasspath(Bytes pom, Scope scope);

  Path copy(ArtifactRef artifact, Path directory);

}
