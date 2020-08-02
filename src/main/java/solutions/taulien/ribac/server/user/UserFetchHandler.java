package solutions.taulien.ribac.server.user;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.commons.httpclient.HttpStatus;
import solutions.taulien.ribac.server.ReadOrCreateRequestIdHandler;
import solutions.taulien.ribac.server.Responder;
import solutions.taulien.ribac.server.error.ResourceNotFoundError;
import solutions.taulien.ribac.server.gen.openapi.User;
import solutions.taulien.ribac.server.gen.openapi.UserFetchResponse;

public class UserFetchHandler implements Handler<RoutingContext> {

    private final UserRepository userRepository;

    private final Responder responder;



    @Inject
    public UserFetchHandler(UserRepository userRepository, Responder responder) {
        this.userRepository = userRepository;
        this.responder = responder;
    }



    @Override
    public void handle(RoutingContext ctx) {
        final var externalUserId = ctx.pathParam("userId");

        final String requestId = ctx.get(ReadOrCreateRequestIdHandler.REQUEST_ID_KEY);
        this.userRepository
            .getUser(externalUserId, requestId)
            .subscribe(
                requestedUserRecord -> this.responder
                                           .ok(
                                               ctx,
                                               new UserFetchResponse()
                                                   .requestedUser(new User()
                                                                      .id(requestedUserRecord.getExternalId())
                                                   )
                                           ),
                failure -> ctx.fail(
                    (failure instanceof ResourceNotFoundError)
                        ? ((ResourceNotFoundError) failure).toHttpError(HttpStatus.SC_NOT_FOUND)
                        : failure
                )
            );
    }
}
