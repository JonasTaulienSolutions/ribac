package solutions.taulien.ribac.server.user;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.commons.httpclient.HttpStatus;
import solutions.taulien.ribac.server.ReadOrCreateRequestIdHandler;
import solutions.taulien.ribac.server.Responder;
import solutions.taulien.ribac.server.error.DuplicateCreateError;

public class UserCreateHandler implements Handler<RoutingContext> {

    private final UserRepository userRepository;

    private final Responder responder;



    @Inject
    public UserCreateHandler(UserRepository userRepository, Responder responder) {
        this.userRepository = userRepository;
        this.responder = responder;
    }



    @Override
    public void handle(RoutingContext ctx) {
        final RequestParameters params = ctx.get("parsedParameters");
        final var requestBody = params.body().getJsonObject();
        final var externalId = requestBody.getString("id");

        final String requestId = ctx.get(ReadOrCreateRequestIdHandler.REQUEST_ID_KEY);
        this.userRepository
            .createUser(externalId, requestId)
            .subscribe(
                createdUser -> this.responder
                                   .created(
                                       ctx,
                                       new JsonObject()
                                           .put(
                                               "createdUser", new JsonObject().put(
                                                   "id", createdUser.getExternalId()
                                               )
                                           )
                                   ),
                failure -> ctx.fail(
                    (failure instanceof DuplicateCreateError)
                        ? ((DuplicateCreateError) failure).toHttpError(HttpStatus.SC_CONFLICT)
                        : failure
                )
            );
    }

}
