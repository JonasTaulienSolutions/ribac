package solutions.taulien.ribac.server.error;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;
import solutions.taulien.ribac.server.Responder;
import solutions.taulien.ribac.server.gen.openapi.ApiErrorResponse;
import solutions.taulien.ribac.server.gen.openapi.ApiErrorResponseError;

import static org.apache.commons.httpclient.HttpStatus.SC_BAD_REQUEST;

public class OpenApiValidationFailureHandler implements Handler<RoutingContext> {

    private final Responder responder;



    @Inject
    public OpenApiValidationFailureHandler(Responder responder) {
        this.responder = responder;
    }



    @Override
    public void handle(RoutingContext ctx) {
        final String message = (ctx.failure() != null)
                                   ? ctx.failure().getMessage()
                                   : "Bad Request";

        this.responder
            .respond(
                ctx,
                SC_BAD_REQUEST,
                new ApiErrorResponse()
                    .error(new ApiErrorResponseError()
                               .message(message)
                    )
            );
    }
}
