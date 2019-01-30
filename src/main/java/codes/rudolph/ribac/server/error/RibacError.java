package codes.rudolph.ribac.server.error;

public abstract class RibacError extends Exception {

    public RibacError(String message) {
        super(message);
    }



    public HttpError toHttpError(int httpStatusCode) {
        return HttpError.fromThrowable(this, httpStatusCode);
    }
}
