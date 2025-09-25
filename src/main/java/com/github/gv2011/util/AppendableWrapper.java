package com.github.gv2011.util;

import static com.github.gv2011.util.Verify.tryCast;
import static com.github.gv2011.util.ex.Exceptions.call;

final class AppendableWrapper implements AppendableNt{

  private final Appendable delegate;

  AppendableWrapper(final Appendable delegate) {
    this.delegate = delegate;
  }

  @Override
  public AppendableNt append(final CharSequence csq) {
    call(()->delegate.append(csq));
    return this;
  }

  @Override
  public AppendableNt append(final CharSequence csq, final int start, final int end) {
    call(()->delegate.append(csq, start, end));
    return this;
  }

  @Override
  public AppendableNt append(final char c) {
    call(()->delegate.append(c));
    return this;
  }

  @Override
  public void close() {
    tryCast(delegate, AutoCloseable.class).ifPresentDo(AutoCloseable::close);
  }

}
