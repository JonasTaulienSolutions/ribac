package codes.rudolph.ribac.server;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;

public class ReadOrCreateRequestIdHandler implements Handler<RoutingContext> {

    public static final String REQUEST_ID_KEY = "REQUEST_ID";

    private static final String REQUEST_ID_HEADER = "Request-Id";

    private final Logger log;



    @Inject
    public ReadOrCreateRequestIdHandler(
        @Named("systemLogger") Logger log
    ) {
        this.log = log;
    }



    @Override
    public void handle(RoutingContext ctx) {
        final var requestHeaders = ctx.request().headers();

        final var clientSendRequestId = requestHeaders.contains(REQUEST_ID_HEADER);

        final var requestId = clientSendRequestId
                                  ? requestHeaders.get(REQUEST_ID_HEADER)
                                  : log.createRequestId();

        ctx.put(REQUEST_ID_KEY, requestId);

        ctx.next();
    }
}
