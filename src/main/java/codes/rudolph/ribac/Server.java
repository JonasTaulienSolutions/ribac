package codes.rudolph.ribac;

import com.google.inject.name.Named;
import io.reactivex.Scheduler;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import org.apache.commons.httpclient.HttpStatus;

import javax.inject.Inject;
import java.util.logging.Logger;

public class Server {

    private final HttpServer server;

    private final Scheduler eventLoop;

    private final RightBasedAccessControl rightBasedAccessControl;

    private final Logger log;

    private final int port;



    @Inject
    public Server(
        HttpServer server,
        @Named("eventLoopScheduler") Scheduler eventLoop,
        RightBasedAccessControl rightBasedAccessControl,
        Logger log,
        @Named("serverPort") int port
    ) {
        this.server = server;
        this.eventLoop = eventLoop;
        this.rightBasedAccessControl = rightBasedAccessControl;
        this.log = log;
        this.port = port;
    }



    public void start() {
        final var router = this.rightBasedAccessControl.createRouter();

        this.server.requestStream()
                   .toFlowable()
                   .map(HttpServerRequest::pause)
                   .onBackpressureDrop(Server::respondWithServiceUnavailable)
                   .observeOn(this.eventLoop)
                   .map(HttpServerRequest::resume)
                   .subscribe(
                       router::accept,
                       Throwable::printStackTrace
                   );

        this.server.rxListen(this.port).subscribe(
            (server) -> log.info("Server successfully started on port " + server.actualPort()),
            Throwable::printStackTrace
        );
    }



    private static void respondWithServiceUnavailable(HttpServerRequest req) {
        req.response().setStatusCode(HttpStatus.SC_SERVICE_UNAVAILABLE).end();
    }
}
