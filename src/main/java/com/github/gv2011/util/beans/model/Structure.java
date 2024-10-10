package com.github.gv2011.util.beans.model;

public enum Structure implements ModelElement{

  NONE, OPT, SINGLE, SET, MAP, ELEMENTARY_MAP, LIST;

  Boolean isSet(){
    return !equals(NONE);
  }

}
