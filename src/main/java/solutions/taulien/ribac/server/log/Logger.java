package solutions.taulien.ribac.server.log;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.stream.Stream;

public class Logger {

    private final static org.slf4j.Logger log = LoggerFactory.getLogger(Logger.class);

    private final String logPrefix;

    private final String requestIdPrefix;

    private final Scheduler scheduler;



    public Logger(
        String logPrefix,
        String requestIdPrefix,
        Scheduler scheduler
    ) {
        this.logPrefix = logPrefix;
        this.requestIdPrefix = requestIdPrefix;
        this.scheduler = scheduler;
    }



    public String createRequestId() {
        return this.requestIdPrefix + "-" + UUID.randomUUID().toString();
    }



    public String createExternalRequestId(String requestId) {
        return requestId + " / " + this.createRequestId();
    }



    public void systemInfo(String message, Object... params) {
        doLog(
            () -> log.info(message, params)
        );
    }



    public void start(String message, String requestId, Object... params) {
        doLog(
            () -> log.info("[{}] {}START " + message, prependTo(params, requestId, this.logPrefix))
        );
    }



    public void requestBody(String requestId, String requestBody) {
        doLog(
            () -> log.debug("[{}] {}REQUEST-BODY {}", requestId, this.logPrefix, prepareBodyString(requestBody))
        );
    }



    public void end(String message, String requestId, Object... params) {
        doLog(
            () -> log.info("[{}] {}END " + message, prependTo(params, requestId, this.logPrefix))
        );
    }



    public void responseBody(String requestId, String responseBody) {
        doLog(
            () -> log.debug("[{}] {}RESPONSE-BODY {}", requestId, this.logPrefix, prepareBodyString(responseBody))
        );
    }



    public <T> Consumer<T> endSuccessfullyUsingConsumer(String successfully, String requestId) {
        return element -> this.endSuccessfullyUsingConsumer(successfully, requestId);
    }



    public Action endSuccessfullyUsingAction(String successfully, String requestId) {
        return () -> doLog(
            () -> log.info("[{}] {}END SUCCESS {}", requestId, this.logPrefix, successfully)
        );
    }



    public <T> Consumer<T> endFailed(String failed, String requestId) {
        return throwable -> doLog(
            () -> log.error("[" + requestId + "] " + this.logPrefix + "END FAILED " + failed + ":", throwable)
        );
    }



    private void doLog(Runnable logAction) {
        Completable.fromRunnable(logAction)
                   .subscribeOn(this.scheduler)
                   .subscribe();
    }



    private static Object[] prependTo(Object[] tail, Object... head) {
        return Stream.of(head, tail)
                     .flatMap(Stream::of)
                     .toArray(Object[]::new);
    }



    private static String prepareBodyString(String body) {
        return body.isEmpty()
                   ? "<empty>"
                   : body;
    }
}
