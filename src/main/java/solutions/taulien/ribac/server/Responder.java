package solutions.taulien.ribac.server;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.vertx.reactivex.ext.web.RoutingContext;

import static org.apache.http.HttpStatus.*;

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
