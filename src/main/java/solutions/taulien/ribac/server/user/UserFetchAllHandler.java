package solutions.taulien.ribac.server.user;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;
import solutions.taulien.ribac.server.ReadOrCreateRequestIdHandler;
import solutions.taulien.ribac.server.Responder;
import solutions.taulien.ribac.server.gen.openapi.User;
import solutions.taulien.ribac.server.gen.openapi.UserFetchAllResponse;
import solutions.taulien.ribac.server.util.FunctionalHelper;

public class UserFetchAllHandler implements Handler<RoutingContext> {

    private final UserRepository userRepository;

    private final Responder responder;



    @Inject
    public UserFetchAllHandler(UserRepository userRepository, Responder responder) {
        this.userRepository = userRepository;
        this.responder = responder;
    }



    @Override
    public void handle(RoutingContext ctx) {
        final String requestId = ctx.get(ReadOrCreateRequestIdHandler.REQUEST_ID_KEY);

        this.userRepository
            .getAllUsers(requestId)
            .subscribe(
                allUsers -> this.responder
                                .ok(
                                    ctx,
                                    new UserFetchAllResponse()
                                        .allUsers(FunctionalHelper.mapAll(allUsers, user -> new User().id(user.getExternalId())))
                                ),
                ctx::fail
            );
    }
}
