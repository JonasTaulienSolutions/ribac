package codes.rudolph.ribac;

import io.vertx.core.json.JsonObject;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserFunctionalTest {

    @BeforeEach
    void beforeEach() throws IOException, InterruptedException {
        RibacTestHelper.createRibacDb();
    }



    @AfterEach
    void afterEach() throws InterruptedException, IOException {
        RibacTestHelper.destroyRibacDb();
    }



    @Test
    void createUser_returnsCreatedUser() {
        final var client = RibacTestHelper.createHttpClient();

        final var id = "user123";

        final var createUserResponse = client.post("/users")
                                             .putHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                                             .rxSendJsonObject(new JsonObject().put(
                                                 "id", id
                                             ))
                                             .blockingGet();
        assertEquals(
            HttpStatus.SC_CREATED,
            createUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + createUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            ContentType.APPLICATION_JSON.getMimeType(),
            createUserResponse.getHeader(HttpHeaders.CONTENT_TYPE)
        );

        final var expectedCreateUserBody = new JsonObject().put(
            "createdUser", new JsonObject().put(
                "id", id
            )
        );
        assertEquals(expectedCreateUserBody, createUserResponse.bodyAsJsonObject());
    }



    @Test
    void createUser_canNotCreateUserTwice() {
        final var client = RibacTestHelper.createHttpClient();

        final var id = "user123";

        // 1. Create
        // noinspection ResultOfMethodCallIgnored
        client.post("/users")
              .putHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
              .rxSendJsonObject(new JsonObject().put(
                  "id", id
              ))
              .blockingGet();

        // 2. Try to create again
        final var createUserSecondResponse = client.post("/users")
                                                   .putHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                                                   .rxSendJsonObject(new JsonObject().put(
                                                       "id", id
                                                   ))
                                                   .blockingGet();

        assertEquals(
            HttpStatus.SC_CONFLICT,
            createUserSecondResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + createUserSecondResponse.bodyAsString() + "'"
        );

        final var expectedCreateUserBody = new JsonObject().put(
            "error", new JsonObject().put(
                "message", "A user already exists with the id '" + id + "'"
            )
        );
        assertEquals(expectedCreateUserBody, createUserSecondResponse.bodyAsJsonObject());
    }



    @Test
    @Disabled
    void createUser_canNotCreateUserWithEmptyExternalId() {

    }



    @Test
    @Disabled
    void createUser_canNotCreateUserWithTooLongExternalId() {

    }



    @Test
    @Disabled
    void createUser_canNotCreateUserWithExternalIdAsNumber() {

    }



    @Test
    @Disabled
    void createUser_canNotCreateUserWhenNotProvidingExternalIdField() {

    }



    @Test
    @Disabled
    void createUser_canNotCreateUserWhenProvidingEmptyBody() {

    }



    @Test
    @Disabled
    void fetchUser_returnsRequestedUser() {

    }



    @Test
    @Disabled
    void fetchUser_canNotReturnUnknownUser() {

    }



    @Test
    @Disabled
    void fetchUser_canNotReturnUserWithEmptyExternalId() {

    }



    @Test
    @Disabled
    void fetchUser_canNotReturnUserWithTooLongExternalId() {

    }



    @Test
    @Disabled
    void deleteUser_userWasDeleted() {

    }



    @Test
    @Disabled
    void deleteUser_canNotDeleteNotExistingUser() {

    }



    @Test
    @Disabled
    void deleteUser_canNotDeleteWithEmptyExternalId() {

    }



    @Test
    @Disabled
    void deleteUser_canNotDeleteWhenNotProvidingExternalIdField() {

    }



    @Test
    @Disabled
    void deleteUser_canNotDeleteWhenProvidingEmptyBody() {

    }



    @Test
    @Disabled
    void deleteUser_alsoDeletesEveryGroupMembership() {

    }



    @Test
    @Disabled
    void deleteUser_alsoDeletesEveryUserRight() {

    }



    @Test
    @Disabled
    void deleteUser_alsoDeletesEveryUserRights() {

    }

    //TODO: Change id
}