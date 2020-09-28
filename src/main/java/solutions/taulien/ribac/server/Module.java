package solutions.taulien.ribac.server;

import com.google.inject.AbstractModule;
import com.zaxxer.hikari.HikariDataSource;
import io.reactivex.Scheduler;
import io.vertx.core.http.HttpMethod;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import io.vertx.reactivex.ext.web.handler.CorsHandler;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import solutions.taulien.ribac.server.log.Logger;

import static com.google.inject.name.Names.named;

public class Module extends AbstractModule {

    protected void configure() {
        final var env = System.getenv();
        final var databaseHost = env.get("RIBAC_DB_HOST");
        final var databasePort = env.get("RIBAC_DB_PORT");
        final var databaseName = env.get("RIBAC_DB_NAME");
        final var databaseUser = env.get("RIBAC_DB_USER");
        final var databasePassword = env.get("RIBAC_DB_PASSWORD");

        final var vertx = Vertx.vertx();

        bind(HttpServer.class).toInstance(vertx.createHttpServer());

        bind(Scheduler.class).annotatedWith(named("eventLoopScheduler")).toInstance(RxHelper.scheduler(vertx));
        final var workerPoolScheduler = RxHelper.blockingScheduler(vertx, false);
        bind(Scheduler.class).annotatedWith(named("workerPoolScheduler")).toInstance(workerPoolScheduler);

        bind(Integer.class).annotatedWith(named("serverPort")).toInstance(8080);

        final var dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(
            "jdbc:mysql://"
                + databaseHost
                + ":"
                + databasePort
                + "/"
                + databaseName
                + "?autoReconnect=true"
                + "&useSSL=false"
        );
        dataSource.setUsername(databaseUser);
        dataSource.setPassword(databasePassword);
        // Recommended by https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        dataSource.addDataSourceProperty("cachePrepStmts", true);
        dataSource.addDataSourceProperty("prepStmtCacheSize", 250);
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        dataSource.addDataSourceProperty("useServerPrepStmts", true);
        dataSource.addDataSourceProperty("useLocalSessionState", true);
        dataSource.addDataSourceProperty("rewriteBatchedStatements", true);
        dataSource.addDataSourceProperty("cacheResultSetMetadata", true);
        dataSource.addDataSourceProperty("cacheServerConfiguration", true);
        dataSource.addDataSourceProperty("elideSetAutoCommits", true);
        dataSource.addDataSourceProperty("maintainTimeStats", false);

        dataSource.setConnectionTimeout(5000);

        final var jooqConfig = new DefaultConfiguration();
        jooqConfig.setDataSource(dataSource);
        jooqConfig.setSQLDialect(SQLDialect.MYSQL_8_0);
        bind(Configuration.class).toInstance(jooqConfig);

        bind(OpenAPI3RouterFactory.class).toInstance(
            OpenAPI3RouterFactory.rxCreate(vertx, "ribac.yaml").blockingGet()
        );

        bind(CorsHandler.class).toInstance(
            CorsHandler.create("*")
                       .allowedMethod(HttpMethod.POST)
                       .allowedMethod(HttpMethod.GET)
                       .allowedMethod(HttpMethod.OPTIONS)
                       .allowedMethod(HttpMethod.DELETE)
                       .allowedMethod(HttpMethod.PATCH)
                       .allowedMethod(HttpMethod.PUT)
                       .allowedHeader("Access-Control-Allow-Origin")
                       .allowedHeader("Origin")
                       .allowedHeader("Content-Type")
                       .allowedHeader("Accept")
                       .allowedHeader("Request-Id")
        );

        bind(Logger.class).annotatedWith(named("dbLogger")).toInstance(new Logger("DB communication: ", "DB", workerPoolScheduler));
        bind(Logger.class).annotatedWith(named("systemLogger")).toInstance(new Logger("", "selfgen", workerPoolScheduler));
    }
}