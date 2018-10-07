package codes.rudolph.ribac;

import codes.rudolph.ribac.user.create.UserCreateHandler;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

import javax.inject.Inject;

public class RightBasedAccessControl {

    private final Router router;

    private final UserCreateHandler userCreateHandler;



    @Inject
    public RightBasedAccessControl(Router router, UserCreateHandler userCreateHandler) {
        this.router = router;
        this.userCreateHandler = userCreateHandler;
    }



    public Router getRouter() {
        this.router.route().handler(BodyHandler.create());

        this.router.post("/users").handler(this.userCreateHandler);

        return this.router;
    }
}
