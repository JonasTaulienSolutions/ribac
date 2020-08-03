package solutions.taulien.ribac.server.error;

import io.reactivex.functions.Consumer;
import io.vertx.reactivex.ext.web.RoutingContext;
import solutions.taulien.ribac.server.util.FunctionalHelper;
import solutions.taulien.ribac.server.util.Tuple;

import java.util.Arrays;

public abstract class RibacError extends Exception {

    public RibacError(String message) {
        super(message);
    }



    @SafeVarargs
    public static Consumer<? super Throwable> mapErrors(
        RoutingContext ctx,
        Tuple<Class<? extends RibacError>, Integer>... mappings
    ) {
        return throwable -> ctx.fail(
            FunctionalHelper.reduce(
                Arrays.asList(mappings),
                throwable,
                (t, mapping) -> RibacError.mapToHttpError(t, mapping.getLeft(), mapping.getRight())
            )
        );
    }



    private static Throwable mapToHttpError(
        Throwable failure,
        Class<? extends RibacError> clazz,
        int httpStatusCode
    ) {
        return (clazz.isInstance(failure))
                   ? ((RibacError) failure).mapToHttpError(httpStatusCode)
                   : failure;
    }



    public HttpError mapToHttpError(int httpStatusCode) {
        return HttpError.fromThrowable(this, httpStatusCode);
    }
}
