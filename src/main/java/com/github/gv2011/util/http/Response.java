package com.github.gv2011.util.http;

import com.github.gv2011.util.beans.Bean;

public interface Response extends HttpMessage, Bean{

  StatusCode statusCode();

}
