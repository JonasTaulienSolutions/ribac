package codes.rudolph.ribac.server;

import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.util.UUID;

public class ReadOrCreateRequestIdHandler implements Handler<RoutingContext> {

    public static final String REQUEST_ID_KEY = "REQUEST_ID";

    private static final String REQUEST_ID_HEADER = "Request-Id";

    private static final String SELFGEN_REQUEST_ID_PREFIX = "selfgen-";



    @Override
    public void handle(RoutingContext ctx) {
        final var requestHeaders = ctx.request().headers();

        final var clientSendRequestId = requestHeaders.contains(REQUEST_ID_HEADER);

        final var requestId = clientSendRequestId
            ? requestHeaders.get(REQUEST_ID_HEADER)
            : SELFGEN_REQUEST_ID_PREFIX + UUID.randomUUID().toString();

        ctx.put(REQUEST_ID_KEY, requestId);

        ctx.next();
    }
}
