package solutions.taulien.ribac.server.user;

import solutions.taulien.ribac.server.ReadOrCreateRequestIdHandler;
import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.commons.httpclient.HttpStatus;

public class UserFetchHandler implements Handler<RoutingContext> {

    private final UserRepository userRepository;



    @Inject
    public UserFetchHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    @Override
    public void handle(RoutingContext ctx) {
        final var externalUserId = ctx.pathParam("userId");

        final String requestId = ctx.get(ReadOrCreateRequestIdHandler.REQUEST_ID_KEY);
        this.userRepository
            .getUser(externalUserId, requestId)
            .subscribe(
                requestedUser -> ctx.response()
                                    .setStatusCode(HttpStatus.SC_OK)
                                    .end(
                                        new JsonObject()
                                            .put(
                                                "requestedUser", new JsonObject().put(
                                                    "id", requestedUser.getExternalId()
                                                )
                                            )
                                            .encode()
                                    ),
                ctx::fail
            );
    }
}
