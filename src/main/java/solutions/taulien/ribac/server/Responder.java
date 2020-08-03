package solutions.taulien.ribac.server;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpHeaders;
import io.vertx.reactivex.ext.web.RoutingContext;
import solutions.taulien.ribac.server.log.Logger;
import solutions.taulien.ribac.server.log.ReadOrCreateRequestIdHandler;

import static org.apache.commons.httpclient.HttpStatus.*;

public class Responder {

    private final Logger log;



    @Inject
    public Responder(
        @Named("systemLogger") Logger log
    ) {
        this.log = log;
    }



    public void ok(RoutingContext ctx, Object body) {
        this.respond(ctx, SC_OK, body);
    }



    public void noContent(RoutingContext ctx) {
        this.respond(ctx, SC_NO_CONTENT);
    }



    public void created(RoutingContext ctx, Object body) {
        this.respond(ctx, SC_CREATED, body);
    }



    public void internalServerError(RoutingContext ctx, Object body) {
        this.respond(ctx, SC_INTERNAL_SERVER_ERROR, body);
    }



    public void respond(RoutingContext ctx, int statusCode, Object body) {
        final String requestId = ctx.get(ReadOrCreateRequestIdHandler.REQUEST_ID_KEY);
        final var bodyAsString = io.vertx.core.json.JsonObject.mapFrom(body).encode();

        this.log.responseBody(requestId, bodyAsString);
        this.log.end("{}", requestId, statusCode);

        ctx.response()
           .setStatusCode(statusCode)
           .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString())
           .putHeader(HttpHeaders.CACHE_CONTROL, HttpHeaderValues.NO_STORE.toString())
           .end(bodyAsString);
    }



    public void respond(RoutingContext ctx, int statusCode) {
        final String requestId = ctx.get(ReadOrCreateRequestIdHandler.REQUEST_ID_KEY);

        this.log.responseBody(requestId, "");
        this.log.end("{}", requestId, statusCode);

        ctx.response()
           .setStatusCode(statusCode)
           .end();
    }
}
