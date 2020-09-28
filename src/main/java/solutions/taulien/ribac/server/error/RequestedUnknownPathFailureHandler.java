package solutions.taulien.ribac.server.error;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;
import solutions.taulien.ribac.server.Responder;

import static org.apache.commons.httpclient.HttpStatus.SC_NOT_FOUND;

public class RequestedUnknownPathFailureHandler implements Handler<RoutingContext> {

    private final Responder responder;



    @Inject
    public RequestedUnknownPathFailureHandler(Responder responder) {
        this.responder = responder;
    }



    @Override
    public void handle(RoutingContext ctx) {
        this.responder
            .respondError(
                ctx,
                SC_NOT_FOUND,
                "Unknown path requested"
            );
    }
}
