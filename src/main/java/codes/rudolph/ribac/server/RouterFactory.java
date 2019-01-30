package codes.rudolph.ribac.server;

import codes.rudolph.ribac.server.error.HttpErrorHandler;
import codes.rudolph.ribac.server.error.InternalServerErrorFailureHandler;
import codes.rudolph.ribac.server.error.OpenApiValidationFailureHandler;
import codes.rudolph.ribac.server.user.UserCreateHandler;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.contract.RouterFactoryOptions;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;

import javax.inject.Inject;

public class RouterFactory {

    private final OpenAPI3RouterFactory openAPI3RouterFactory;

    private final UserCreateHandler userCreateHandler;

    private final InternalServerErrorFailureHandler internalServerErrorFailureHandler;

    private final OpenApiValidationFailureHandler validationFailureHandler;

    private final HttpErrorHandler httpErrorHandler;



    @Inject
    public RouterFactory(
        OpenAPI3RouterFactory openAPI3RouterFactory,
        UserCreateHandler userCreateHandler,
        InternalServerErrorFailureHandler internalServerErrorFailureHandler,
        OpenApiValidationFailureHandler validationFailureHandler,
        HttpErrorHandler httpErrorHandler
    ) {
        this.openAPI3RouterFactory = openAPI3RouterFactory;
        this.userCreateHandler = userCreateHandler;
        this.internalServerErrorFailureHandler = internalServerErrorFailureHandler;
        this.validationFailureHandler = validationFailureHandler;
        this.httpErrorHandler = httpErrorHandler;
    }



    public Router create() {
        this.addHandler("createUser", this.userCreateHandler);
//        this.addHandler("fetchUser", this.userFetchHandler);

        final var options = new RouterFactoryOptions()
            .setMountNotImplementedHandler(true) //Default
            .setMountResponseContentTypeHandler(true) //Default
            .setMountValidationFailureHandler(true) //Default
            .setValidationFailureHandler(this.validationFailureHandler)
            .setRequireSecurityHandlers(true);  //Default

        return this.openAPI3RouterFactory.setOptions(options)
                                         .getRouter();
    }



    private void addHandler(String operationId, Handler<RoutingContext> handler) {
        this.openAPI3RouterFactory.addHandlerByOperationId(operationId, handler);
        this.openAPI3RouterFactory.addFailureHandlerByOperationId(operationId, this.httpErrorHandler);
        this.openAPI3RouterFactory.addFailureHandlerByOperationId(operationId, this.internalServerErrorFailureHandler);
    }
}
