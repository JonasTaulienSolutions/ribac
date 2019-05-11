package codes.rudolph.ribac.server;

import com.google.inject.name.Named;
import io.reactivex.Scheduler;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class Server {

    private final static Logger log = LoggerFactory.getLogger(Server.class);

    private final HttpServer server;

    private final Scheduler eventLoop;

    private final RouterFactory routerFactory;

    private final int port;



    @Inject
    public Server(
        HttpServer server,
        @Named("eventLoopScheduler") Scheduler eventLoop,
        RouterFactory routerFactory,
        @Named("serverPort") int port
    ) {
        this.server = server;
        this.eventLoop = eventLoop;
        this.routerFactory = routerFactory;
        this.port = port;
    }



    public void start() {
        final var router = this.routerFactory.create();

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
            (server) -> log.info("Server successfully started on port {}", server.actualPort()),
            Throwable::printStackTrace
        );
    }



    private static void respondWithServiceUnavailable(HttpServerRequest req) {
        req.response().setStatusCode(HttpStatus.SC_SERVICE_UNAVAILABLE).end();
    }
}
