package codes.rudolph.ribac.server;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.validation.ValidationException;
import org.apache.commons.httpclient.HttpStatus;

public class OpenApiValidationFailureHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        if (ctx.failure() instanceof ValidationException) {
            ctx.response()
               .setStatusCode(HttpStatus.SC_BAD_REQUEST)
               .end(
                   new JsonObject()
                       .put(
                           "error", new JsonObject().put(
                               "message", ctx.failure().getMessage()
                           )
                       )
                       .encode()
               );
        } else {
            ctx.next();
        }
    }
}
