package solutions.taulien.ribac.server;

import com.google.inject.name.Named;
import io.vertx.reactivex.core.http.HttpServer;

import javax.inject.Inject;

public class Server {

    private final Logger log;

    private final HttpServer server;

    private final RouterFactory routerFactory;

    private final int port;



    @Inject
    public Server(
        @Named("systemLogger") Logger log,
        HttpServer server,
        RouterFactory routerFactory,
        @Named("serverPort") int port
    ) {
        this.log = log;
        this.server = server;
        this.routerFactory = routerFactory;
        this.port = port;
    }



    public void start() {
        final var router = this.routerFactory.create();

        this.server.requestHandler(router)
                   .rxListen(this.port)
                   .subscribe(
                       (server) -> log.systemInfo("Server successfully started on port {}", server.actualPort()),
                       Throwable::printStackTrace
                   );
    }
}
