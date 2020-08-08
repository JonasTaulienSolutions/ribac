package solutions.taulien.ribac.server.group;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;
import solutions.taulien.ribac.server.Responder;
import solutions.taulien.ribac.server.gen.openapi.ApiGroup;
import solutions.taulien.ribac.server.gen.openapi.ApiGroupFetchAllResponse;
import solutions.taulien.ribac.server.log.ReadOrCreateRequestIdHandler;
import solutions.taulien.ribac.server.util.FunctionalHelper;

public class GroupFetchAllHandler implements Handler<RoutingContext> {

    private final GroupRepository groupRepository;

    private final Responder responder;



    @Inject
    public GroupFetchAllHandler(GroupRepository groupRepository, Responder responder) {
        this.groupRepository = groupRepository;
        this.responder = responder;
    }



    @Override
    public void handle(RoutingContext ctx) {
        final String requestId = ctx.get(ReadOrCreateRequestIdHandler.REQUEST_ID_KEY);

        this.groupRepository
            .getAllGroups(requestId)
            .subscribe(
                allGroups -> this.responder
                                 .ok(
                                     ctx,
                                     new ApiGroupFetchAllResponse()
                                         .allGroups(FunctionalHelper.mapAll(allGroups, group -> new ApiGroup().name(group.getName())))
                                 )
            );
    }
}
