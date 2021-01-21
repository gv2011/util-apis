package com.github.gv2011.util.net;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.net.InetAddress;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;

public final class NetUtils {
  
  private NetUtils(){staticClass();}


  public static final Constant<NetUtilsSpi> NET_UTILS_PROVIDER =
    RecursiveServiceLoader.lazyService(NetUtilsSpi.class)
  ;
  
  
  public static final InetAddress forString(String ipString) {
    return NET_UTILS_PROVIDER.get().forString(ipString);
  }

  public static final boolean isInetAddress(String ipString) {
    return NET_UTILS_PROVIDER.get().isInetAddress(ipString);
  }
}
