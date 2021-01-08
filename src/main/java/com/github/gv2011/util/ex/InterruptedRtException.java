package com.github.gv2011.util.ex;

import java.io.InterruptedIOException;

public class InterruptedRtException extends RuntimeException{

  public InterruptedRtException(final InterruptedException e) {
    super(e);
  }

  public InterruptedRtException(final InterruptedIOException e) {
    super(e);
  }

  public InterruptedRtException(final InterruptedException e, final String msg) {
    super(msg, e);
  }

  public InterruptedRtException(final InterruptedIOException e, final String msg) {
    super(msg, e);
  }

}
