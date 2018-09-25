package codes.rudolph.ribac;

import com.google.inject.name.Named;
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

    private final int port;



    @Inject
    public Server(
        HttpServer server,
        Router router,
        Scheduler scheduler,
        RightBasedAccessControl rightBasedAccessControl,
        @Named("serverPort") int port
    ) {
        this.server = server;
        this.router = router;
        this.scheduler = scheduler;
        this.rightBasedAccessControl = rightBasedAccessControl;
        this.port = port;
    }



    public void start() {
        this.router.mountSubRouter("/", this.rightBasedAccessControl.getRouter());

        this.server.requestStream()
                   .toFlowable()
                   .map(HttpServerRequest::pause)
                   .onBackpressureDrop(Server::respondWithServiceUnavailable)
                   .observeOn(this.scheduler)
                   .map(HttpServerRequest::resume)
                   .subscribe(
                       this.router::accept,
                       Throwable::printStackTrace
                   );

        this.server.rxListen(this.port).subscribe(
            (server) -> System.out.println("Server successfully started on port " + server.actualPort()),
            Throwable::printStackTrace
        );
    }



    private static void respondWithServiceUnavailable(HttpServerRequest req) {
        req.response().setStatusCode(HttpStatus.SC_SERVICE_UNAVAILABLE).end();
    }
}
