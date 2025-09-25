package com.github.gv2011.util.ssh;

public final class AuthenticationFailedException extends RuntimeException{

  public AuthenticationFailedException(final Throwable cause) {
    super(cause.getMessage(), cause);
  }

}
