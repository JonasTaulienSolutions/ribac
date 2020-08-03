package solutions.taulien.ribac.server.user;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.commons.httpclient.HttpStatus;
import solutions.taulien.ribac.server.Responder;
import solutions.taulien.ribac.server.error.DuplicateCreateError;
import solutions.taulien.ribac.server.error.RibacError;
import solutions.taulien.ribac.server.gen.openapi.ApiUser;
import solutions.taulien.ribac.server.gen.openapi.ApiUserCreateResponse;
import solutions.taulien.ribac.server.log.ReadOrCreateRequestIdHandler;
import solutions.taulien.ribac.server.util.Tuple;

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
        final var userToCreate = params.body().getJsonObject().mapTo(ApiUser.class);

        final String requestId = ctx.get(ReadOrCreateRequestIdHandler.REQUEST_ID_KEY);
        this.userRepository
            .createUser(userToCreate.getId(), requestId)
            .subscribe(
                createdUser -> this.responder
                                   .created(
                                       ctx,
                                       new ApiUserCreateResponse()
                                           .createdUser(new ApiUser()
                                                            .id(createdUser.getExternalId())
                                           )
                                   ),
                RibacError.mapErrors(
                    ctx,
                    Tuple.of(DuplicateCreateError.class, HttpStatus.SC_CONFLICT)
                )
            );
    }

}
