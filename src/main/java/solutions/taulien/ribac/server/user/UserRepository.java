package solutions.taulien.ribac.server.user;

import com.google.inject.Inject;
import io.reactivex.Completable;
import io.reactivex.Single;
import org.jooq.exception.DataAccessException;
import solutions.taulien.ribac.server.DbHelper;
import solutions.taulien.ribac.server.error.DuplicateCreateError;
import solutions.taulien.ribac.server.error.ResourceNotFoundError;
import solutions.taulien.ribac.server.gen.jooq.tables.records.DbUser;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static solutions.taulien.ribac.server.gen.jooq.Tables.USER;

public class UserRepository {

    private final DbHelper dbHelper;



    @Inject
    public UserRepository(
        DbHelper dbHelper
    ) {
        this.dbHelper = dbHelper;
    }



    public Single<DbUser> createUser(String externalId, String requestId) {
        return this.dbHelper
                   .execute(
                       requestId,
                       db -> db.insertInto(USER)
                               .set(USER.EXTERNAL_ID, externalId)
                               .returning()
                               .fetchOne()
                   )
                   .onErrorResumeNext(
                       failure -> Single.error(
                           UserRepository.mySqlRespondedWithDuplicateEntryError(failure)
                               ? new DuplicateCreateError("A user already exists with the id '" + externalId + "'")
                               : failure
                       )
                   );
    }



    public Single<DbUser> getUser(String externalId, String requestId) {
        return this.dbHelper
                   .execute(
                       requestId,
                       db -> Optional.ofNullable(
                           db.selectFrom(USER)
                             .where(USER.EXTERNAL_ID.eq(externalId))
                             .fetchOne()
                       )
                   )
                   .map(maybeUser -> maybeUser.orElseThrow(
                       () -> new ResourceNotFoundError("A user with the id '" + externalId + "' does not exist")
                   ));
    }



    public Single<List<DbUser>> getAllUsers(String requestId) {
        return this.dbHelper
                   .execute(
                       requestId,
                       db -> db.select()
                               .from(USER)
                               .fetch()
                               .into(DbUser.class)
                   );
    }



    public Completable deleteUser(String externalId, String requestId) {
        return this.dbHelper
                   .execute(
                       requestId,
                       db -> db.deleteFrom(USER)
                               .where(USER.EXTERNAL_ID.eq(externalId))
                               .execute()
                   )
                   .map(numberOfDeletedRecords -> {
                            if (numberOfDeletedRecords == 0) {
                                throw new ResourceNotFoundError("A user with the id '" + externalId + "' does not exist");
                            }

                            return numberOfDeletedRecords;
                        }
                   )
                   .ignoreElement();
    }



    private static boolean mySqlRespondedWithDuplicateEntryError(Throwable failure) {
        /*
         * @see https://dev.mysql.com/doc/refman/8.0/en/server-error-reference.html#error_er_dup_entry
         */
        final int MYSQL_DUPLICATE_ENTRY_CODE = 1062;

        return (failure instanceof DataAccessException)
                   && (failure.getCause() instanceof SQLException)
                   && (((SQLException) failure.getCause()).getErrorCode() == MYSQL_DUPLICATE_ENTRY_CODE);
    }
}
