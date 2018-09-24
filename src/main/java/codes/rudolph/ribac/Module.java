package codes.rudolph.ribac;

import com.google.inject.AbstractModule;
import io.reactivex.Scheduler;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;

public class Module extends AbstractModule {
    protected void configure() {
        final var vertx = Vertx.vertx();

        bind(HttpServer.class).toInstance(vertx.createHttpServer());

        // Inject a new Router instance each time
        bind(Router.class).toProvider(() -> Router.router(vertx));

        bind(Scheduler.class).toInstance(RxHelper.scheduler(vertx));
    }
}