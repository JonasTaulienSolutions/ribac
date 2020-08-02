package solutions.taulien.ribac.server.log;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;

public class LogRequestStartHandler implements Handler<RoutingContext> {

    private final Logger log;



    @Inject
    public LogRequestStartHandler(
        @Named("systemLogger") Logger log
    ) {
        this.log = log;
    }



    @Override
    public void handle(RoutingContext ctx) {
        final String requestId = ctx.get(ReadOrCreateRequestIdHandler.REQUEST_ID_KEY);

        final var method = ctx.request().method().toString();
        final var path = ctx.request().path();
        log.start("{} {}", requestId, method, path);

        final var body = ctx.getBodyAsString();
        log.requestBody(requestId, body);

        ctx.next();
    }
}
