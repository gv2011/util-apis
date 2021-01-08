package com.github.gv2011.util.http;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Opt;

public interface HttpFactory {

  RestClient createRestClient();

  HttpServer createServer(IList<Pair<Space,RequestHandler>> handlers);

  HttpServer createServer(IList<Pair<Space,RequestHandler>> handlers, int httpPort);

  default Response createResponse() {
    return createResponse(statusNotFound(), Opt.empty());
  }

  default Response createResponse(final TypedBytes entity) {
    return createResponse(statusOk(), Opt.of(entity));
  }

  Response createResponse(StatusCode statusCode, Opt<TypedBytes> entity);
  
  StatusCode statusOk();

  StatusCode statusNotFound();

}
