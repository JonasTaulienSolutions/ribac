package codes.rudolph.ribac;

import io.vertx.reactivex.ext.web.Router;

import javax.inject.Inject;

public class RightBasedAccessControl {

    private final Router router;



    @Inject
    public RightBasedAccessControl(Router router) {
        this.router = router;
    }



    public Router getRouter() {
        this.router.route("/test").handler(
            ctx -> ctx.response().setStatusCode(200).end("Test ok")
        );

        return this.router;
    }
}
