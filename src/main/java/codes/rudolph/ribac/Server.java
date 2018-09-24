package codes.rudolph.ribac;


import io.reactivex.Scheduler;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.Router;
import org.apache.commons.httpclient.HttpStatus;

import javax.inject.Inject;

public class Server {

    private final HttpServer server;

    private final Router router;

    private final Scheduler scheduler;

    private final RightBasedAccessControl rightBasedAccessControl;

    @Inject
    public Server(HttpServer server, Router router, Scheduler scheduler, RightBasedAccessControl rightBasedAccessControl) {
        this.server = server;
        this.router = router;
        this.scheduler = scheduler;
        this.rightBasedAccessControl = rightBasedAccessControl;
    }

    public void start() {
        this.router.mountSubRouter("/", this.rightBasedAccessControl.getRouter());

        this.server.requestStream()
                .toFlowable()
                .map(HttpServerRequest::pause)
                .onBackpressureDrop(this::respondServiceUnavailable)
                .observeOn(this.scheduler)
                .map(HttpServerRequest::resume)
                .subscribe(
                        this.router::accept,
                        Throwable::printStackTrace
                );

        this.server.rxListen(8080).subscribe(
                (server) -> System.out.println("Server successfully started on port 8080"),
                Throwable::printStackTrace
        );
    }

    private void respondServiceUnavailable(HttpServerRequest req) {
        req.response().setStatusCode(HttpStatus.SC_SERVICE_UNAVAILABLE).end();
    }
}
