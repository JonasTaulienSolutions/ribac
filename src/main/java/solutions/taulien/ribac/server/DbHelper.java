package solutions.taulien.ribac.server;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import solutions.taulien.ribac.server.log.Logger;

public class DbHelper {

    private final Configuration jooqConfig;

    private final Scheduler workerPool;

    private final Logger log;



    @Inject
    public DbHelper(
        Configuration jooqConfig,
        @Named("workerPoolScheduler") Scheduler workerPool,
        @Named("dbLogger") Logger log
    ) {
        this.jooqConfig = jooqConfig;
        this.workerPool = workerPool;
        this.log = log;
    }



    /**
     * Executes the given action on a worker pool thread inside a transaction and returns the result as a Single
     *
     * @param dbGetter The action to execute inside of the transaction. Must return a result.
     * @param <R>      The type of the result
     * @return a {@link Single} containing whatever the dbGetter returns
     */
    public <R> Single<R> execute(String requestId, Function<DSLContext, R> dbGetter) {
        //TODO: Handle no connection could be established

        final var externalRequestId = this.log.createExternalRequestId(requestId);

        this.log.start("Executing statement(s)", externalRequestId);
        return Single
                   .fromCallable(
                       () -> DSL.using(this.jooqConfig).transactionResult(
                           transactionConfig -> dbGetter.apply(DSL.using(transactionConfig))
                       )
                   )
                   .onErrorResumeNext(throwable -> Single.error(
                       ((throwable instanceof DataAccessException) && "Rollback caused".equals(throwable.getMessage()))
                           ? throwable.getCause()
                           : throwable
                   ))
                   .subscribeOn(this.workerPool)
                   .doOnSuccess(this.log.endSuccessfullyUsingConsumer("Executed statement(s)", externalRequestId))
                   .doOnError(this.log.endFailed("To execute statement(s)", externalRequestId));
    }
}
