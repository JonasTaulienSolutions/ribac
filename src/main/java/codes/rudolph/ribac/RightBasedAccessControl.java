package codes.rudolph.ribac;

import codes.rudolph.ribac.user.create.UserCreateHandler;
import io.vertx.ext.web.api.contract.RouterFactoryOptions;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;

import javax.inject.Inject;

public class RightBasedAccessControl {

    private final OpenAPI3RouterFactory openAPI3RouterFactory;

    private final UserCreateHandler userCreateHandler;



    @Inject
    public RightBasedAccessControl(
        OpenAPI3RouterFactory openAPI3RouterFactory,
        UserCreateHandler userCreateHandler
    ) {
        this.openAPI3RouterFactory = openAPI3RouterFactory;
        this.userCreateHandler = userCreateHandler;
    }



    public Router createRouter() {
        this.openAPI3RouterFactory.addHandlerByOperationId("createUser", this.userCreateHandler);

        final var options = new RouterFactoryOptions()
            .setMountNotImplementedHandler(true) //Default
            .setMountResponseContentTypeHandler(true) //Default
            .setMountValidationFailureHandler(true) //Default
            .setRequireSecurityHandlers(true);  //Default

        return this.openAPI3RouterFactory.setOptions(options)
                                         .getRouter();
    }
}
