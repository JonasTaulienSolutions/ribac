package solutions.taulien.ribac.server.error;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;
import solutions.taulien.ribac.server.Responder;
import solutions.taulien.ribac.server.gen.openapi.ApiErrorResponse;
import solutions.taulien.ribac.server.gen.openapi.ApiErrorResponseError;

public class HttpErrorHandler implements Handler<RoutingContext> {

    private final Responder responder;



    @Inject
    public HttpErrorHandler(Responder responder) {
        this.responder = responder;
    }



    @Override
    public void handle(RoutingContext ctx) {
        if (ctx.failure() instanceof HttpError) {
            final HttpError error = (HttpError) ctx.failure();
            this.responder
                .respond(
                    ctx,
                    error.getHttpStatusCode(),
                    new ApiErrorResponse()
                        .error(new ApiErrorResponseError()
                                   .message(error.getMessage())
                        )
                );

        } else {
            ctx.next();
        }
    }
}
