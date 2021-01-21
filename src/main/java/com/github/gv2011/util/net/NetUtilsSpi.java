package com.github.gv2011.util.net;

import java.net.InetAddress;

public interface NetUtilsSpi {

  InetAddress forString(String ipString);

  boolean isInetAddress(String ipString);
  
}
