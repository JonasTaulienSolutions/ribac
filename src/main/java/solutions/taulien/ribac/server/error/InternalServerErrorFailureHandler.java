package solutions.taulien.ribac.server.error;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;
import solutions.taulien.ribac.server.Responder;
import solutions.taulien.ribac.server.gen.openapi.ErrorResponse;
import solutions.taulien.ribac.server.gen.openapi.ErrorResponseError;

public class InternalServerErrorFailureHandler implements Handler<RoutingContext> {

    private final Responder responder;



    @Inject
    public InternalServerErrorFailureHandler(Responder responder) {
        this.responder = responder;
    }



    @Override
    public void handle(RoutingContext ctx) {
        final Throwable failure = ctx.failure();

        this.responder
            .internalServerError(
                ctx,
                new ErrorResponse()
                    .error(new ErrorResponseError()
                               .message(failure.toString())
                    )
            );

    }

}
