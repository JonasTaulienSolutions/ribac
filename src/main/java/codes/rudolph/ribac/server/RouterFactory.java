package codes.rudolph.ribac.server;

import codes.rudolph.ribac.server.error.HttpErrorHandler;
import codes.rudolph.ribac.server.error.InternalServerErrorFailureHandler;
import codes.rudolph.ribac.server.error.OpenApiValidationFailureHandler;
import codes.rudolph.ribac.server.user.UserCreateHandler;
import codes.rudolph.ribac.server.user.UserFetchHandler;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.contract.RouterFactoryOptions;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import io.vertx.reactivex.ext.web.handler.CorsHandler;

import javax.inject.Inject;

public class RouterFactory {

    private final OpenAPI3RouterFactory openAPI3RouterFactory;

    private final InternalServerErrorFailureHandler internalServerErrorFailureHandler;

    private final OpenApiValidationFailureHandler validationFailureHandler;

    private final HttpErrorHandler httpErrorHandler;

    private final CorsHandler corsHandler;

    private final UserCreateHandler userCreateHandler;

    private final UserFetchHandler userFetchHandler;

    private final ReadOrCreateRequestIdHandler readOrCreateRequestIdHandler;



    @Inject
    public RouterFactory(
        OpenAPI3RouterFactory openAPI3RouterFactory,
        UserCreateHandler userCreateHandler,
        InternalServerErrorFailureHandler internalServerErrorFailureHandler,
        OpenApiValidationFailureHandler validationFailureHandler,
        HttpErrorHandler httpErrorHandler,
        CorsHandler corsHandler,
        UserFetchHandler userFetchHandler,
        ReadOrCreateRequestIdHandler readOrCreateRequestIdHandler
    ) {
        this.openAPI3RouterFactory = openAPI3RouterFactory;
        this.userCreateHandler = userCreateHandler;
        this.internalServerErrorFailureHandler = internalServerErrorFailureHandler;
        this.validationFailureHandler = validationFailureHandler;
        this.httpErrorHandler = httpErrorHandler;
        this.corsHandler = corsHandler;
        this.userFetchHandler = userFetchHandler;
        this.readOrCreateRequestIdHandler = readOrCreateRequestIdHandler;
    }



    public Router create() {
        this.addHandler("userCreate", this.userCreateHandler);
        this.addHandler("userFetch", this.userFetchHandler);

        final var options = new RouterFactoryOptions()
            .setMountNotImplementedHandler(true) //Default
            .setMountResponseContentTypeHandler(true) //Default
            .setMountValidationFailureHandler(true) //Default
            .setValidationFailureHandler(this.validationFailureHandler)
            .setRequireSecurityHandlers(true)  //Default
            .addGlobalHandler(ctx -> this.readOrCreateRequestIdHandler.handle(RoutingContext.newInstance(ctx)))
            .addGlobalHandler(ctx -> this.corsHandler.handle(RoutingContext.newInstance(ctx)));

        return this.openAPI3RouterFactory.setOptions(options)
                                         .getRouter();
    }



    private void addHandler(String operationId, Handler<RoutingContext> handler) {
        this.openAPI3RouterFactory.addHandlerByOperationId(operationId, handler);
        this.openAPI3RouterFactory.addFailureHandlerByOperationId(operationId, this.httpErrorHandler);
        this.openAPI3RouterFactory.addFailureHandlerByOperationId(operationId, this.internalServerErrorFailureHandler);
    }
}
