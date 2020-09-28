package solutions.taulien.ribac.server.group;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.commons.httpclient.HttpStatus;
import solutions.taulien.ribac.server.Responder;
import solutions.taulien.ribac.server.error.ResourceNotFoundError;
import solutions.taulien.ribac.server.error.RibacError;
import solutions.taulien.ribac.server.gen.openapi.ApiGroup;
import solutions.taulien.ribac.server.gen.openapi.ApiGroupFetchResponse;
import solutions.taulien.ribac.server.log.ReadOrCreateRequestIdHandler;
import solutions.taulien.ribac.server.util.Tuple;

public class GroupFetchHandler implements Handler<RoutingContext> {

    private final GroupRepository groupRepository;

    private final Responder responder;



    @Inject
    public GroupFetchHandler(GroupRepository groupRepository, Responder responder) {
        this.groupRepository = groupRepository;
        this.responder = responder;
    }



    @Override
    public void handle(RoutingContext ctx) {
        final var groupName = ctx.pathParam("groupName");

        final String requestId = ctx.get(ReadOrCreateRequestIdHandler.REQUEST_ID_KEY);
        this.groupRepository
            .getGroup(groupName, requestId)
            .subscribe(
                requestedGroup -> this.responder
                                      .ok(
                                          ctx,
                                          new ApiGroupFetchResponse().requestedGroup(new ApiGroup().name(requestedGroup.getName()))
                                      ),
                RibacError.mapErrors(
                    ctx,
                    Tuple.of(ResourceNotFoundError.class, HttpStatus.SC_NOT_FOUND)
                )
            );
    }
}