package solutions.taulien.ribac.server;

import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import io.vertx.reactivex.ext.web.handler.CorsHandler;
import solutions.taulien.ribac.server.error.HttpErrorHandler;
import solutions.taulien.ribac.server.error.InternalServerErrorFailureHandler;
import solutions.taulien.ribac.server.error.OpenApiValidationFailureHandler;
import solutions.taulien.ribac.server.group.GroupCreateHandler;
import solutions.taulien.ribac.server.group.GroupDeleteHandler;
import solutions.taulien.ribac.server.group.GroupFetchAllHandler;
import solutions.taulien.ribac.server.group.GroupFetchHandler;
import solutions.taulien.ribac.server.log.LogRequestStartHandler;
import solutions.taulien.ribac.server.log.ReadOrCreateRequestIdHandler;
import solutions.taulien.ribac.server.user.UserCreateHandler;
import solutions.taulien.ribac.server.user.UserDeleteHandler;
import solutions.taulien.ribac.server.user.UserFetchAllHandler;
import solutions.taulien.ribac.server.user.UserFetchHandler;

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

    private final LogRequestStartHandler logRequestStartHandler;

    private final UserDeleteHandler userDeleteHandler;

    private final UserFetchAllHandler userFetchAllHandler;

    private final GroupCreateHandler groupCreateHandler;

    private final GroupFetchAllHandler groupFetchAllHandler;

    private final GroupDeleteHandler groupDeleteHandler;

    private final GroupFetchHandler groupFetchHandler;



    @Inject
    public RouterFactory(
        OpenAPI3RouterFactory openAPI3RouterFactory,
        UserCreateHandler userCreateHandler,
        InternalServerErrorFailureHandler internalServerErrorFailureHandler,
        OpenApiValidationFailureHandler validationFailureHandler,
        HttpErrorHandler httpErrorHandler,
        CorsHandler corsHandler,
        UserFetchHandler userFetchHandler,
        ReadOrCreateRequestIdHandler readOrCreateRequestIdHandler,
        LogRequestStartHandler logRequestStartHandler,
        UserDeleteHandler userDeleteHandler,
        UserFetchAllHandler userFetchAllHandler,
        GroupCreateHandler groupCreateHandler,
        GroupFetchAllHandler groupFetchAllHandler,
        GroupDeleteHandler groupDeleteHandler,
        GroupFetchHandler groupFetchHandler
    ) {
        this.openAPI3RouterFactory = openAPI3RouterFactory;
        this.userCreateHandler = userCreateHandler;
        this.internalServerErrorFailureHandler = internalServerErrorFailureHandler;
        this.validationFailureHandler = validationFailureHandler;
        this.httpErrorHandler = httpErrorHandler;
        this.corsHandler = corsHandler;
        this.userFetchHandler = userFetchHandler;
        this.readOrCreateRequestIdHandler = readOrCreateRequestIdHandler;
        this.logRequestStartHandler = logRequestStartHandler;
        this.userDeleteHandler = userDeleteHandler;
        this.userFetchAllHandler = userFetchAllHandler;
        this.groupCreateHandler = groupCreateHandler;
        this.groupFetchAllHandler = groupFetchAllHandler;
        this.groupDeleteHandler = groupDeleteHandler;
        this.groupFetchHandler = groupFetchHandler;
    }



    public Router create() {
        this.openAPI3RouterFactory.addGlobalHandler(this.corsHandler)
                                  .addGlobalHandler(this.readOrCreateRequestIdHandler)
                                  .addGlobalHandler(this.logRequestStartHandler);

        this.addHandler("userCreate", this.userCreateHandler);
        this.addHandler("userFetch", this.userFetchHandler);
        this.addHandler("userDelete", this.userDeleteHandler);
        this.addHandler("userFetchAll", this.userFetchAllHandler);

        this.addHandler("groupCreate", this.groupCreateHandler);
        this.addHandler("groupFetchAll", this.groupFetchAllHandler);
        this.addHandler("groupDelete", this.groupDeleteHandler);
        this.addHandler("groupFetch", this.groupFetchHandler);

        return this.openAPI3RouterFactory.getRouter()
                                         .errorHandler(400, this.validationFailureHandler)
                                         .errorHandler(500, this.internalServerErrorFailureHandler);
    }



    private void addHandler(String operationId, Handler<RoutingContext> handler) {
        this.openAPI3RouterFactory.addHandlerByOperationId(operationId, handler);
        this.openAPI3RouterFactory.addFailureHandlerByOperationId(operationId, this.httpErrorHandler);
    }
}
