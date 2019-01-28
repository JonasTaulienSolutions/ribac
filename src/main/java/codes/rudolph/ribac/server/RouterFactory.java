package codes.rudolph.ribac.server;

import codes.rudolph.ribac.server.user.create.UserCreateHandler;
import io.vertx.ext.web.api.contract.RouterFactoryOptions;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;

import javax.inject.Inject;

public class RouterFactory {

    private final OpenAPI3RouterFactory openAPI3RouterFactory;

    private final UserCreateHandler userCreateHandler;

    private final CatchAllFailureHandler catchAllFailureHandler;

    private final OpenApiValidationFailureHandler validationFailureHandler;



    @Inject
    public RouterFactory(
        OpenAPI3RouterFactory openAPI3RouterFactory,
        UserCreateHandler userCreateHandler,
        CatchAllFailureHandler catchAllFailureHandler,
        OpenApiValidationFailureHandler validationFailureHandler
    ) {
        this.openAPI3RouterFactory = openAPI3RouterFactory;
        this.userCreateHandler = userCreateHandler;
        this.catchAllFailureHandler = catchAllFailureHandler;
        this.validationFailureHandler = validationFailureHandler;
    }



    public Router create() {
        this.openAPI3RouterFactory.addHandlerByOperationId("createUser", this.userCreateHandler);
        this.openAPI3RouterFactory.addFailureHandlerByOperationId("createUser", this.catchAllFailureHandler);

        final var options = new RouterFactoryOptions()
            .setMountNotImplementedHandler(true) //Default
            .setMountResponseContentTypeHandler(true) //Default
            .setMountValidationFailureHandler(true) //Default
            .setValidationFailureHandler(this.validationFailureHandler)
            .setRequireSecurityHandlers(true);  //Default

        return this.openAPI3RouterFactory.setOptions(options)
                                         .getRouter();
    }
}
