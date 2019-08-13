package solutions.taulien.ribac.server;

import io.reactivex.functions.Consumer;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.stream.Stream;

public class Logger {

    private final static org.slf4j.Logger log = LoggerFactory.getLogger(Logger.class);

    private final String logPrefix;

    private final String requestIdPrefix;



    public Logger(String logPrefix, String requestIdPrefix) {
        this.logPrefix = logPrefix;
        this.requestIdPrefix = requestIdPrefix;
    }



    public String createRequestId() {
        return this.requestIdPrefix + "-" + UUID.randomUUID().toString();
    }



    public String createExternalRequestId(String requestId) {
        return requestId + " / " + this.createRequestId();
    }



    public void systemInfo(String message, Object... params) {
        log.info(message, params);
    }



    public void start(String message, String requestId, Object... params) {
        log.info("[{}] {}START " + message, prependTo(params, requestId, this.logPrefix));
    }



    public void requestBody(String requestId, String requestBody) {
        log.info("[{}] {}REQUEST-BODY {}", requestId, this.logPrefix, prepareBodyString(requestBody));
    }



    public <T> Consumer<T> endSuccessfully(String successfully, String requestId) {
        return element -> log.info("[{}] {}END SUCCESS {}", requestId, this.logPrefix, successfully);
    }



    public <T> Consumer<T> endFailed(String failed, String requestId) {
        return throwable -> log.error("[" + requestId + "] " + this.logPrefix + "END FAILED " + failed + ":", throwable);
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
