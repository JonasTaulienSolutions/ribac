package codes.rudolph.ribac.server.user;

import codes.rudolph.ribac.server.error.ConflictError;
import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.commons.httpclient.HttpStatus;

public class UserCreateHandler implements Handler<RoutingContext> {

    private final UserRepository userRepository;



    @Inject
    public UserCreateHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    @Override
    public void handle(RoutingContext ctx) {
        final RequestParameters params = ctx.get("parsedParameters");
        final var requestBody = params.body().getJsonObject();
        final var externalId = requestBody.getString("id");

        this.userRepository
            .createUser(externalId)
            .subscribe(
                createdUser -> ctx.response()
                                  .setStatusCode(HttpStatus.SC_CREATED)
                                  .end(
                                      new JsonObject()
                                          .put(
                                              "createdUser", new JsonObject().put(
                                                  "id", createdUser.getExternalId()
                                              )
                                          )
                                          .encode()
                                  ),
                failure -> ctx.fail(
                    (failure instanceof ConflictError)
                        ? ((ConflictError) failure).toHttpError(HttpStatus.SC_CONFLICT)
                        : failure
                )
            );
    }

}
