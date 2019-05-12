package codes.rudolph.ribac.server;

import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static codes.rudolph.ribac.server.ReadOrCreateRequestIdHandler.REQUEST_ID_KEY;

public class LogRequestStartHandler implements Handler<RoutingContext> {

    private final static Logger log = LoggerFactory.getLogger(LogRequestStartHandler.class);



    @Override
    public void handle(RoutingContext ctx) {
        final var requestId = ctx.get(REQUEST_ID_KEY);

        final var method = ctx.request().method().toString();
        final var path = ctx.request().path();
        log.info("[{}] START: {} {}", requestId, method, path);

        final var body = ctx.getBodyAsString();
        final var logBody = body.isEmpty()
            ? "<empty>"
            : body;
        log.debug("[{}] Request-Body: {}", requestId, logBody);

        ctx.next();
    }
}
