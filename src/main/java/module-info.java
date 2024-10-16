import com.github.gv2011.util.ann.Artifact;

@Artifact(groupId = "com.github.gv2011", artifactId = "util-apis")
module com.github.gv2011.util{
  requires transitive org.slf4j;
  requires transitive java.management;
  requires transitive java.xml;
  requires transitive java.naming;
  requires transitive jakarta.activation;
  requires transitive java.sql;
  requires transitive java.desktop;
  
  requires com.google.common;

  uses com.github.gv2011.util.icol.ICollectionFactorySupplier;
  uses com.github.gv2011.util.beans.TypeRegistry;
  uses com.github.gv2011.util.json.JsonFactory;

  exports com.github.gv2011.util;
  exports com.github.gv2011.util.ann;
  exports com.github.gv2011.util.beans;
  exports com.github.gv2011.util.bytes;
  exports com.github.gv2011.util.cache;
  exports com.github.gv2011.util.context;
  exports com.github.gv2011.util.download;
  exports com.github.gv2011.util.email;
  exports com.github.gv2011.util.ex;
  exports com.github.gv2011.util.filewatch;
  exports com.github.gv2011.util.jdbc;
  exports com.github.gv2011.util.html;
  exports com.github.gv2011.util.http;
  exports com.github.gv2011.util.icol;
  exports com.github.gv2011.util.json;
  exports com.github.gv2011.util.legacy;
  exports com.github.gv2011.util.lock;
  exports com.github.gv2011.util.log;
  exports com.github.gv2011.util.m2t;
  exports com.github.gv2011.util.main;
  exports com.github.gv2011.util.net;
  exports com.github.gv2011.util.num;
  exports com.github.gv2011.util.sec;
  exports com.github.gv2011.util.serviceloader;
  exports com.github.gv2011.util.streams;
  exports com.github.gv2011.util.swing;
  exports com.github.gv2011.util.table;
  exports com.github.gv2011.util.tempfile;
  exports com.github.gv2011.util.time;
  exports com.github.gv2011.util.tstr;
  exports com.github.gv2011.util.uc;
  exports com.github.gv2011.util.xml;

  exports com.github.gv2011.util.internal to com.github.gv2011.util.beans.imp;
}
