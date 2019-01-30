package codes.rudolph.ribac.server.error;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

public class HttpErrorHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        if (ctx.failure() instanceof HttpError) {
            final HttpError error = (HttpError) ctx.failure();
            ctx.response()
               .setStatusCode(error.getHttpStatusCode())
               .end(
                   new JsonObject()
                       .put(
                           "error", new JsonObject().put(
                               "message", error.getMessage()
                           )
                       )
                       .encode()
               );

        } else {
            ctx.next();
        }
    }
}
