package codes.rudolph.ribac;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.http.HttpStatus;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FailureHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        ctx.response()
           .setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
           .end(FailureHandler.exceptionToJson(ctx.failure()).encode());

    }



    private static JsonObject exceptionToJson(Throwable e) {
        return new JsonObject()
            .put("error", new JsonObject()
                .put("message", e.toString())
                .put(
                    "stacktrace",
                    new JsonArray(
                        Stream.of(e.getStackTrace())
                              .map(StackTraceElement::toString)
                              .collect(Collectors.toList())
                    )
                )
            );
    }
}
