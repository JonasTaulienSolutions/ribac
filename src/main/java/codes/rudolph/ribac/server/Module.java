package codes.rudolph.ribac.server;

import com.google.inject.AbstractModule;
import com.zaxxer.hikari.HikariDataSource;
import io.reactivex.Scheduler;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;

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
        bind(Scheduler.class).annotatedWith(named("workerPoolScheduler")).toInstance(RxHelper.blockingScheduler(vertx));

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

        final var jooqConfig = new DefaultConfiguration();
        jooqConfig.setDataSource(dataSource);
        jooqConfig.setSQLDialect(SQLDialect.MYSQL_8_0);
        bind(Configuration.class).toInstance(jooqConfig);

        bind(OpenAPI3RouterFactory.class).toInstance(
            OpenAPI3RouterFactory.rxCreate(vertx, "ribac.yaml").blockingGet()
        );
    }
}