package com.github.gv2011.util.m2t;

import com.github.gv2011.util.StringUtils;

public enum Scope {
  COMPILE, RUNTIME;
  @Override
  public String toString(){return StringUtils.toLowerCase(name());}
}