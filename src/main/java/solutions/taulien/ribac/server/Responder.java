package solutions.taulien.ribac.server;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.vertx.core.json.JsonObject;
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



    public void ok(RoutingContext ctx, JsonObject body) {
        this.respond(ctx, SC_OK, body);
    }



    public void created(RoutingContext ctx, JsonObject body) {
        this.respond(ctx, SC_CREATED, body);
    }



    public void internalServerError(RoutingContext ctx, JsonObject body) {
        this.respond(ctx, SC_INTERNAL_SERVER_ERROR, body);
    }



    public void respond(RoutingContext ctx, int statusCode, JsonObject body) {
        final String requestId = ctx.get(ReadOrCreateRequestIdHandler.REQUEST_ID_KEY);

        this.log.end("{}", requestId, statusCode);
        this.log.responseBody(requestId, body.encode());

        ctx.response()
           .setStatusCode(statusCode)
           .end(body.encode());
    }
}
