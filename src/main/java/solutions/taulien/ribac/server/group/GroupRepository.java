package solutions.taulien.ribac.server.group;

import com.google.inject.Inject;
import io.reactivex.Single;
import solutions.taulien.ribac.server.DbHelper;
import solutions.taulien.ribac.server.error.DuplicateCreateError;
import solutions.taulien.ribac.server.gen.jooq.tables.records.DbGroup;
import solutions.taulien.ribac.server.util.RepositoryHelper;

import java.util.List;

import static solutions.taulien.ribac.server.gen.jooq.Tables.GROUP;

public class GroupRepository {

    private final DbHelper dbHelper;



    @Inject
    public GroupRepository(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }



    public Single<DbGroup> createGroup(String name, String requestId) {
        return this.dbHelper
                   .execute(
                       requestId,
                       db -> db.insertInto(GROUP)
                               .set(GROUP.NAME, name)
                               .returning()
                               .fetchOne()
                   )
                   .onErrorResumeNext(
                       failure -> Single.error(
                           RepositoryHelper.mySqlRespondedWithDuplicateEntryError(failure)
                               ? new DuplicateCreateError("A Group already exists with the name '" + name + "'")
                               : failure
                       )
                   );
    }



    public Single<List<DbGroup>> getAllGroups(String requestId) {
        return this.dbHelper
                   .execute(
                       requestId,
                       db -> db.select()
                               .from(GROUP)
                               .fetch()
                               .into(DbGroup.class)
                   );
    }
}
