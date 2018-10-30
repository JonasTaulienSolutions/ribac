package codes.rudolph.ribac.user.create;

import codes.rudolph.ribac.DbHelper;
import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.commons.httpclient.HttpStatus;

import static codes.rudolph.ribac.jooq.tables.RibacUser.RIBAC_USER;

public class UserCreateHandler implements Handler<RoutingContext> {

    private final DbHelper dbHelper;



    @Inject
    public UserCreateHandler(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }



    @Override
    public void handle(RoutingContext ctx) {
        final RequestParameters params = ctx.get("parsedParameters");
        final var requestBody = params.body().getJsonObject();
        final var externalId = requestBody.getString("id");

        this.dbHelper
            .execute(
                //TODO: Catch user with given externalId already exists
                db -> db.insertInto(RIBAC_USER)
                        .set(RIBAC_USER.EXTERNAL_ID, externalId)
                        .returning()
                        .fetchOne()
            )
            .subscribe(
                createdUser -> ctx.response()
                                  .setStatusCode(HttpStatus.SC_CREATED)
                                  .end(
                                      new JsonObject()
                                          .put(
                                              "user", new JsonObject().put(
                                                  "id", createdUser.getExternalId()
                                              )
                                          )
                                          .encode()
                                  ),
                ctx::fail
            );
    }
}
