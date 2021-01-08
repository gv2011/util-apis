package com.github.gv2011.util.http;

import com.github.gv2011.util.tstr.TypedString;

public interface HostName extends TypedString<HostName>{
  
  public static final HostName LOCALHOST = parse("localhost");
  
  public static HostName parse(String hostName){
    return TypedString.create(HostName.class, hostName);
  }

}
