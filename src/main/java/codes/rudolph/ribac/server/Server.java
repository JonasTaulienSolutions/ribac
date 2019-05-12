package codes.rudolph.ribac.server;

import com.google.inject.name.Named;
import io.vertx.reactivex.core.http.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class Server {

    private final static Logger log = LoggerFactory.getLogger(Server.class);

    private final HttpServer server;

    private final RouterFactory routerFactory;

    private final int port;



    @Inject
    public Server(
        HttpServer server,
        RouterFactory routerFactory,
        @Named("serverPort") int port
    ) {
        this.server = server;
        this.routerFactory = routerFactory;
        this.port = port;
    }



    public void start() {
        final var router = this.routerFactory.create();

        this.server.requestHandler(router::accept);

        this.server.rxListen(this.port).subscribe(
            (server) -> log.info("Server successfully started on port {}", server.actualPort()),
            Throwable::printStackTrace
        );
    }
}
