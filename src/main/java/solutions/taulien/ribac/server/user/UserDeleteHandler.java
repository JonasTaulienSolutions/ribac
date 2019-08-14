package solutions.taulien.ribac.server.user;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;
import solutions.taulien.ribac.server.ReadOrCreateRequestIdHandler;
import solutions.taulien.ribac.server.Responder;

public class UserDeleteHandler implements Handler<RoutingContext> {

    private final UserRepository userRepository;

    private final Responder responder;



    @Inject
    public UserDeleteHandler(UserRepository userRepository, Responder responder) {
        this.userRepository = userRepository;
        this.responder = responder;
    }



    @Override
    public void handle(RoutingContext ctx) {
        final var externalUserId = ctx.pathParam("userId");

        final String requestId = ctx.get(ReadOrCreateRequestIdHandler.REQUEST_ID_KEY);

        this.userRepository
            .deleteUser(externalUserId, requestId)
            .subscribe(
                () -> this.responder.noContent(ctx),
                ctx::fail
            );
    }
}
