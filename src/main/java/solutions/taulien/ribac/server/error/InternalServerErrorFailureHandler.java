package solutions.taulien.ribac.server.error;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;
import solutions.taulien.ribac.server.Responder;

import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                new JsonObject()
                    .put("error", new JsonObject()
                                      .put("message", failure.toString())
                                      .put(
                                          "stacktrace",
                                          new JsonArray(
                                              Stream.of(failure.getStackTrace())
                                                    .map(StackTraceElement::toString)
                                                    .collect(Collectors.toList())
                                          )
                                      )
                    )
            );

    }

}
