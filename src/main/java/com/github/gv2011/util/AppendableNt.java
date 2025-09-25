package com.github.gv2011.util;

public interface AppendableNt extends Appendable, AutoCloseableNt{

  public static AppendableNt wrap(final Appendable a){
    return a instanceof AppendableNt ? (AppendableNt) a : new AppendableWrapper(a);
  }

  @Override
  AppendableNt append(CharSequence csq);

  @Override
  AppendableNt append(CharSequence csq, int start, int end);

  @Override
  AppendableNt append(char c);

}
