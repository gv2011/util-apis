package com.github.gv2011.util.m2t;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.beans.DefaultValue;

public interface ArtifactRef extends Bean{

  GroupId groupId();

  ArtifactId artifactId();

  Version version();

  @DefaultValue("")
  Classifier classifier();

  Type type();


  /**
   * see <a href="https://maven.apache.org/plugins/maven-dependency-plugin/copy-mojo.html">dependency
   * plugin copy target</a>
   */
  static String toString(final ArtifactRef r){
    final Classifier cl = r.classifier();
    final Type type = r.type();
    return r.groupId()+":"+r.artifactId()+":"+r.version()+
      (cl.isEmpty()
        ? (type.toString().equals("jar") ? "" : ":"+r.type())
        :":"+r.type()+":"+cl
      )
     ;
  }

}
