package codes.rudolph.ribac.server.error;

public class HttpError extends Exception {

    private final int httpStatusCode;



    public HttpError(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }



    public static HttpError fromThrowable(Throwable throwable, int httpStatusCode) {
        return new HttpError(throwable.getMessage(), httpStatusCode);
    }



    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
