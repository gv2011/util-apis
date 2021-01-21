package com.github.gv2011.util.http;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.OptionalInt;
import java.util.function.Predicate;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.sec.Domain;

public interface HttpFactory {
  
  static final OptionalInt SERVER_SELECTS_PORT = OptionalInt.of(0);
  static final Path ACME_PROD = Paths.get(".acme-prod");
  static final Path ACME_STAGING = Paths.get(".acme-staging");

  RestClient createRestClient();

  HttpServer createServer(IList<Pair<Space,RequestHandler>> handlers);

  HttpServer createServer(
    IList<Pair<Space,RequestHandler>> handlers, 
    OptionalInt httpPort,
    Opt<CertificateHandler> certHandler,
    Predicate<Domain> isHttpsHost,
    OptionalInt httpsPort
  );

  HttpServer createServer(
    IList<Pair<Space,RequestHandler>> handlers, 
    OptionalInt httpPort,
    OptionalInt httpsPort,
    AcmeStore acmeStore
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
  
  default AcmeStore openAcmeStore(boolean production){
    return openAcmeStore(production ? ACME_PROD : ACME_STAGING);
  }

  AcmeStore openAcmeStore(Path directory);

}
