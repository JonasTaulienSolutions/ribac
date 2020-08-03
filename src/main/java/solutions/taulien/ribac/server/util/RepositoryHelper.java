package solutions.taulien.ribac.server.util;

import org.jooq.exception.DataAccessException;

import java.sql.SQLException;

public class RepositoryHelper {

    public static boolean mySqlRespondedWithDuplicateEntryError(Throwable failure) {
        /*
         * @see https://dev.mysql.com/doc/refman/8.0/en/server-error-reference.html#error_er_dup_entry
         */
        final int MYSQL_DUPLICATE_ENTRY_CODE = 1062;

        return (failure instanceof DataAccessException)
                   && (failure.getCause() instanceof SQLException)
                   && (((SQLException) failure.getCause()).getErrorCode() == MYSQL_DUPLICATE_ENTRY_CODE);
    }
}
