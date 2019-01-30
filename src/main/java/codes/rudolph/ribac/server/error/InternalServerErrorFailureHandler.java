package codes.rudolph.ribac.server.error;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.http.HttpStatus;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InternalServerErrorFailureHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        final Throwable failure = ctx.failure();

        ctx.response()
           .setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
           .end(
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
                   .encode()
           );

    }

}
