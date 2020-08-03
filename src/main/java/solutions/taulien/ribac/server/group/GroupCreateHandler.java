package solutions.taulien.ribac.server.group;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.commons.httpclient.HttpStatus;
import solutions.taulien.ribac.server.Responder;
import solutions.taulien.ribac.server.error.DuplicateCreateError;
import solutions.taulien.ribac.server.error.RibacError;
import solutions.taulien.ribac.server.gen.openapi.ApiGroup;
import solutions.taulien.ribac.server.gen.openapi.ApiGroupCreateResponse;
import solutions.taulien.ribac.server.log.ReadOrCreateRequestIdHandler;
import solutions.taulien.ribac.server.util.Tuple;

public class GroupCreateHandler implements Handler<RoutingContext> {

    private final GroupRepository groupRepository;

    private final Responder responder;



    @Inject
    public GroupCreateHandler(GroupRepository groupRepository, Responder responder) {
        this.groupRepository = groupRepository;
        this.responder = responder;
    }



    @Override
    public void handle(RoutingContext ctx) {
        final RequestParameters params = ctx.get("parsedParameters");
        final var groupToCreate = params.body().getJsonObject().mapTo(ApiGroup.class);

        final String requestId = ctx.get(ReadOrCreateRequestIdHandler.REQUEST_ID_KEY);
        this.groupRepository
            .createGroup(groupToCreate.getName(), requestId)
            .subscribe(
                createdGroup -> this.responder
                                    .created(
                                        ctx,
                                        new ApiGroupCreateResponse().createdGroup(new ApiGroup().name(createdGroup.getName()))
                                    ),
                RibacError.mapErrors(
                    ctx,
                    Tuple.of(DuplicateCreateError.class, HttpStatus.SC_CONFLICT)
                )
            );
    }
}
