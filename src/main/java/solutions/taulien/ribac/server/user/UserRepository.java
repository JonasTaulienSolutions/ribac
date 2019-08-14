package solutions.taulien.ribac.server.user;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.reactivex.Completable;
import io.reactivex.Single;
import org.jooq.exception.DataAccessException;
import solutions.taulien.ribac.server.DbHelper;
import solutions.taulien.ribac.server.Logger;
import solutions.taulien.ribac.server.error.DuplicateCreateError;
import solutions.taulien.ribac.server.error.ResourceNotFoundError;
import solutions.taulien.ribac.server.gen.jooq.tables.records.RibacUserRecord;

import java.sql.SQLException;
import java.util.Optional;

import static solutions.taulien.ribac.server.gen.jooq.tables.RibacUser.RIBAC_USER;

public class UserRepository {

    private final Logger log;

    private final DbHelper dbHelper;



    @Inject
    public UserRepository(
        @Named("dbLogger") Logger log,
        DbHelper dbHelper
    ) {
        this.log = log;
        this.dbHelper = dbHelper;
    }



    public Single<RibacUserRecord> createUser(String externalId, String requestId) {
        final var externalRequestId = log.createExternalRequestId(requestId);

        log.start("Creating User", externalRequestId);
        return this.dbHelper
                   .execute(
                       db -> db.insertInto(RIBAC_USER)
                               .set(RIBAC_USER.EXTERNAL_ID, externalId)
                               .returning()
                               .fetchOne()
                   )
                   .doOnSuccess(log.endSuccessfullyUsingConsumer("Created User", externalRequestId))
                   .doOnError(log.endFailed("To create User", externalRequestId))
                   .onErrorResumeNext(
                       failure -> Single.error(
                           UserRepository.mySqlRespondedWithDuplicateEntryError(failure)
                               ? new DuplicateCreateError("A user already exists with the id '" + externalId + "'")
                               : failure
                       )
                   );
    }



    public Single<RibacUserRecord> getUser(String externalId, String requestId) {
        final var externalRequestId = log.createExternalRequestId(requestId);

        log.start("Getting User", externalRequestId);
        return this.dbHelper
                   .execute(
                       db -> Optional.ofNullable(
                           db.selectFrom(RIBAC_USER)
                             .where(RIBAC_USER.EXTERNAL_ID.eq(externalId))
                             .fetchOne()
                       )
                   )
                   .map(maybeUser -> maybeUser.orElseThrow(
                       () -> new ResourceNotFoundError("A user with the id '" + externalId + "' does not exist")
                   ))
                   .doOnSuccess(log.endSuccessfullyUsingConsumer("Got User", externalRequestId))
                   .doOnError(log.endFailed("To get User", externalRequestId));
    }



    public Completable deleteUser(String externalId, String requestId) {
        final var externalRequestId = log.createExternalRequestId(requestId);

        log.start("Deleting User", externalRequestId);

        return this.dbHelper
                   .execute(
                       db -> db.deleteFrom(RIBAC_USER)
                               .where(RIBAC_USER.EXTERNAL_ID.eq(externalId))
                               .execute()
                   )
                   .map(numberOfDeletedRecords -> {
                            if (numberOfDeletedRecords == 0) {
                                throw new ResourceNotFoundError("A user with the id '" + externalId + "' does not exist");
                            }

                            return numberOfDeletedRecords;
                        }
                   )
                   .ignoreElement()
                   .doOnComplete(log.endSuccessfullyUsingAction("Deleted User", externalRequestId))
                   .doOnError(log.endFailed("To delete User", externalRequestId));
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
