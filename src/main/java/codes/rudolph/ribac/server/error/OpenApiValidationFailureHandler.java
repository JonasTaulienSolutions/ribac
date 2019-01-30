package codes.rudolph.ribac.server.error;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.validation.ValidationException;
import org.apache.commons.httpclient.HttpStatus;

public class OpenApiValidationFailureHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        if (ctx.failure() instanceof ValidationException) {
            ctx.fail(HttpError.fromThrowable(ctx.failure(), HttpStatus.SC_BAD_REQUEST));

        } else {
            ctx.next();
        }
    }
}
