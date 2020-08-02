package solutions.taulien.ribac.server.user;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.commons.httpclient.HttpStatus;
import solutions.taulien.ribac.server.log.ReadOrCreateRequestIdHandler;
import solutions.taulien.ribac.server.Responder;
import solutions.taulien.ribac.server.error.DuplicateCreateError;
import solutions.taulien.ribac.server.gen.openapi.User;
import solutions.taulien.ribac.server.gen.openapi.UserCreateResponse;

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
        final var userToCreate = params.body().getJsonObject().mapTo(User.class);

        final String requestId = ctx.get(ReadOrCreateRequestIdHandler.REQUEST_ID_KEY);
        this.userRepository
            .createUser(userToCreate.getId(), requestId)
            .subscribe(
                createdUserRecord -> this.responder
                                         .created(
                                             ctx,
                                             new UserCreateResponse()
                                                 .createdUser(new User()
                                                                  .id(createdUserRecord.getExternalId())
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
