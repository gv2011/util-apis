package com.github.gv2011.util.http;

import java.util.OptionalInt;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.sec.Domain;

public interface HttpFactory {
  
  static final OptionalInt SERVER_SELECTS_PORT = OptionalInt.of(0);

  RestClient createRestClient();

  HttpServer createServer(IList<Pair<Space,RequestHandler>> handlers);

  HttpServer createServer(
    IList<Pair<Space,RequestHandler>> handlers, 
    OptionalInt httpPort,
    Opt<CertificateHandler> certHandler,
    ISet<Domain> httpsHosts,
    OptionalInt httpsPort
  );

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
