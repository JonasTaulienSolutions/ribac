package codes.rudolph.ribac.server.user;

import codes.rudolph.ribac.jooq.tables.records.RibacUserRecord;
import codes.rudolph.ribac.server.DbHelper;
import codes.rudolph.ribac.server.error.DuplicateCreateError;
import com.google.inject.Inject;
import io.reactivex.Single;
import org.jooq.exception.DataAccessException;

import java.sql.SQLException;

import static codes.rudolph.ribac.jooq.tables.RibacUser.RIBAC_USER;

public class UserRepository {

    private final DbHelper dbHelper;



    @Inject
    public UserRepository(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }



    public Single<RibacUserRecord> createUser(String externalId) {
        return this.dbHelper
            .execute(
                db -> db.insertInto(RIBAC_USER)
                        .set(RIBAC_USER.EXTERNAL_ID, externalId)
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



    public Single<RibacUserRecord> getUser(String externalId) {
        return this.dbHelper
            .execute(
                db -> db.selectFrom(RIBAC_USER)
                        .where(RIBAC_USER.EXTERNAL_ID.eq(externalId))
                        .fetchOne()
            );
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
