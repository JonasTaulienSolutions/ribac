package codes.rudolph.ribac.server.user.create;

import codes.rudolph.ribac.server.DbHelper;
import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.commons.httpclient.HttpStatus;
import org.jooq.exception.DataAccessException;

import java.sql.SQLException;

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
                                              "createdUser", new JsonObject().put(
                                                  "id", createdUser.getExternalId()
                                              )
                                          )
                                          .encode()
                                  ),
                failure -> {
                    if (UserCreateHandler.isDuplicateEntry(failure)) {
                        ctx.response()
                           .setStatusCode(HttpStatus.SC_CONFLICT)
                           .end(
                               new JsonObject()
                                   .put(
                                       "error", new JsonObject().put(
                                           "message", "A user already exists with the id '" + externalId + "'"
                                       )
                                   )
                                   .encode()
                           );

                    } else {
                        ctx.fail(failure);
                    }
                }
            );
    }



    private static boolean isDuplicateEntry(Throwable failure) {
        /*
         * @see https://dev.mysql.com/doc/refman/8.0/en/server-error-reference.html#error_er_dup_entry
         */
        final int MYSQL_DUPLICATE_ENTRY_CODE = 1062;

        return (failure instanceof DataAccessException)
            && (failure.getCause() instanceof SQLException)
            && (((SQLException) failure.getCause()).getErrorCode() == MYSQL_DUPLICATE_ENTRY_CODE);
    }
}
