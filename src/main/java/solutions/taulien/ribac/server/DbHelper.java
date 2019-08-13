package solutions.taulien.ribac.server;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.function.Function;

public class DbHelper {

    private final Configuration jooqConfig;

    private final Scheduler workerPool;



    @Inject
    public DbHelper(
        Configuration jooqConfig,
        @Named("workerPoolScheduler") Scheduler workerPool
    ) {
        this.jooqConfig = jooqConfig;
        this.workerPool = workerPool;
    }



    /**
     * Executes the given action on a worker pool thread inside a transaction and returns the result as a Single
     *
     * @param dbGetter The action to execute inside of the transaction. Must return a result.
     * @param <R> The type of the result
     * @return a {@link Single} containing whatever the dbGetter returns
     */
    public <R> Single<R> execute(Function<DSLContext, R> dbGetter) {
        //TODO: Handle no connection could be established
        return Single
            .fromCallable(
                () -> DSL.using(this.jooqConfig).transactionResult(
                    transactionConfig -> dbGetter.apply(DSL.using(transactionConfig))
                )
            )
            .subscribeOn(this.workerPool);
    }
}
