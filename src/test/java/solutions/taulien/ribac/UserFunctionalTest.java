package solutions.taulien.ribac;

import io.vertx.core.json.JsonObject;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.apache.commons.httpclient.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserFunctionalTest {

    @BeforeAll
    static void beforeAll() throws IOException, InterruptedException {
        RibacTestHelper.createRibacDbBackup();
        RibacTestHelper.destroyRibacDb();
    }



    @BeforeEach
    void beforeEach() throws IOException, InterruptedException {
        RibacTestHelper.createRibacDb();
    }



    @AfterEach
    void afterEach() throws InterruptedException, IOException {
        RibacTestHelper.destroyRibacDb();
    }



    @AfterAll
    static void afterAll() throws InterruptedException, IOException {
        RibacTestHelper.createRibacDb();
        RibacTestHelper.restoreRibacDbBackup();
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
            SC_CREATED,
            createUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + createUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            ContentType.APPLICATION_JSON.getMimeType(),
            createUserResponse.getHeader(HttpHeaders.CONTENT_TYPE)
        );
        assertEquals(
            new JsonObject().put(
                "createdUser", new JsonObject().put(
                    "id", id
                )
            ),
            createUserResponse.bodyAsJsonObject()
        );
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
            SC_CONFLICT,
            createUserSecondResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + createUserSecondResponse.bodyAsString() + "'"
        );
        assertEquals(
            ContentType.APPLICATION_JSON.getMimeType(),
            createUserSecondResponse.getHeader(HttpHeaders.CONTENT_TYPE)
        );
        assertEquals(
            new JsonObject().put(
                "error", new JsonObject().put(
                    "message", "A user already exists with the id '" + id + "'"
                )
            ),
            createUserSecondResponse.bodyAsJsonObject()
        );
    }



    @Test
    void createUser_canNotCreateUserWithEmptyExternalId() {
        final var client = RibacTestHelper.createHttpClient();

        final var createUserResponse = client.post("/users")
                                             .putHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                                             .rxSendJsonObject(new JsonObject().put(
                                                 "id", ""
                                             ))
                                             .blockingGet();
        assertEquals(
            SC_BAD_REQUEST,
            createUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + createUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            ContentType.APPLICATION_JSON.getMimeType(),
            createUserResponse.getHeader(HttpHeaders.CONTENT_TYPE)
        );
        assertEquals(
            new JsonObject().put(
                "error", new JsonObject().put(
                    "message", "$.id: must be at least 1 characters long"
                )
            ),
            createUserResponse.bodyAsJsonObject()
        );
    }



    @Test
    void createUser_canNotCreateUserWithTooLongExternalId() {
        final var client = RibacTestHelper.createHttpClient();

        final var id = "" +
                           "user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345." +
                           "user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345." +
                           "user12345.user12345.user12345.user12345.user12345.12345" +
                           "6";

        final var createUserResponse = client.post("/users")
                                             .putHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                                             .rxSendJsonObject(new JsonObject().put(
                                                 "id", id
                                             ))
                                             .blockingGet();
        assertEquals(
            SC_BAD_REQUEST,
            createUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + createUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            ContentType.APPLICATION_JSON.getMimeType(),
            createUserResponse.getHeader(HttpHeaders.CONTENT_TYPE)
        );
        assertEquals(
            new JsonObject().put(
                "error", new JsonObject().put(
                    "message", "$.id: may only be 255 characters long"
                )
            ),
            createUserResponse.bodyAsJsonObject()
        );
    }



    @Test
    void createUser_canNotCreateUserWithExternalIdAsNumber() {
        final var client = RibacTestHelper.createHttpClient();

        final var createUserResponse = client.post("/users")
                                             .putHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                                             .rxSendJsonObject(new JsonObject().put(
                                                 "id", 12345
                                             ))
                                             .blockingGet();
        assertEquals(
            SC_BAD_REQUEST,
            createUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + createUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            ContentType.APPLICATION_JSON.getMimeType(),
            createUserResponse.getHeader(HttpHeaders.CONTENT_TYPE)
        );
        assertEquals(
            new JsonObject().put(
                "error", new JsonObject().put(
                    "message", "$.id: integer found, string expected"
                )
            ),
            createUserResponse.bodyAsJsonObject()
        );
    }



    @Test
    void createUser_canNotCreateUserWhenNotProvidingExternalIdField() {
        final var client = RibacTestHelper.createHttpClient();

        final var createUserResponse = client.post("/users")
                                             .putHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                                             .rxSendJsonObject(new JsonObject())
                                             .blockingGet();
        assertEquals(
            SC_BAD_REQUEST,
            createUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + createUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            ContentType.APPLICATION_JSON.getMimeType(),
            createUserResponse.getHeader(HttpHeaders.CONTENT_TYPE)
        );
        assertEquals(
            new JsonObject().put(
                "error", new JsonObject().put(
                    "message", "$.id: is missing but it is required"
                )
            ),
            createUserResponse.bodyAsJsonObject()
        );
    }



    @Test
    void createUser_canNotCreateUserWhenProvidingEmptyBody() {
        final var client = RibacTestHelper.createHttpClient();

        final var createUserResponse = client.post("/users")
                                             .putHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                                             .putHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                                             .rxSend()
                                             .blockingGet();
        assertEquals(
            SC_BAD_REQUEST,
            createUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + createUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            ContentType.APPLICATION_JSON.getMimeType(),
            createUserResponse.getHeader(HttpHeaders.CONTENT_TYPE)
        );

        assertEquals(
            new JsonObject().put(
                "error", new JsonObject().put(
                    "message", "$: string found, object expected"
                )
            ),
            createUserResponse.bodyAsJsonObject()
        );
    }



    @Test
    void fetchUser_returnsRequestedUser() {
        final var client = RibacTestHelper.createHttpClient();

        final var id = "user/ 123";

        client.post("/users")
              .putHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
              .rxSendJsonObject(new JsonObject().put(
                  "id", id
              ))
              .blockingGet();

        final var fetchUserResponse = client.get("/users/" + RibacTestHelper.urlEncode(id))
                                            .putHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                                            .rxSend()
                                            .blockingGet();

        assertEquals(
            SC_OK,
            fetchUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + fetchUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            ContentType.APPLICATION_JSON.getMimeType(),
            fetchUserResponse.getHeader(HttpHeaders.CONTENT_TYPE)
        );
        assertEquals(
            new JsonObject().put(
                "requestedUser", new JsonObject().put(
                    "id", id
                )
            ),
            fetchUserResponse.bodyAsJsonObject()
        );
    }



    @Test
    void fetchUser_canNotReturnUnknownUser() {
        final var client = RibacTestHelper.createHttpClient();

        var id = "userB";
        final var fetchUserResponse = client.get("/users/" + RibacTestHelper.urlEncode(id))
                                            .putHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                                            .rxSend()
                                            .blockingGet();

        assertEquals(
            SC_NOT_FOUND,
            fetchUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + fetchUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            ContentType.APPLICATION_JSON.getMimeType(),
            fetchUserResponse.getHeader(HttpHeaders.CONTENT_TYPE)
        );
        assertEquals(
            new JsonObject().put(
                "error", new JsonObject().put(
                    "message", "A user with the id '" + id + "' does not exist"
                )
            ),
            fetchUserResponse.bodyAsJsonObject()
        );
    }



    @Test
    void fetchUser_canNotReturnUserWithEmptyExternalId() {
        final var client = RibacTestHelper.createHttpClient();

        final var fetchUserResponse = client.get("/users/")
                                            .putHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                                            .rxSend()
                                            .blockingGet();

        assertEquals(
            SC_BAD_REQUEST,
            fetchUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + fetchUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            ContentType.APPLICATION_JSON.getMimeType(),
            fetchUserResponse.getHeader(HttpHeaders.CONTENT_TYPE)
        );
        assertEquals(
            new JsonObject().put(
                "error", new JsonObject().put(
                    "message", "Value doesn't respect min length 1"
                )
            ),
            fetchUserResponse.bodyAsJsonObject()
        );
    }



    @Test
    void fetchUser_canNotReturnUserWithTooLongExternalId() {
        final var client = RibacTestHelper.createHttpClient();

        final var id = "" +
                           "user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345." +
                           "user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345." +
                           "user12345.user12345.user12345.user12345.user12345.12345" +
                           "6";
        final var fetchUserResponse = client.get("/users/" + RibacTestHelper.urlEncode(id))
                                            .putHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                                            .rxSend()
                                            .blockingGet();

        assertEquals(
            SC_BAD_REQUEST,
            fetchUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + fetchUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            ContentType.APPLICATION_JSON.getMimeType(),
            fetchUserResponse.getHeader(HttpHeaders.CONTENT_TYPE)
        );
        assertEquals(
            new JsonObject().put(
                "error", new JsonObject().put(
                    "message", "Value doesn't respect max length 255"
                )
            ),
            fetchUserResponse.bodyAsJsonObject()
        );
    }



    @Test
    void deleteUser_userWasDeleted() {
        final var client = RibacTestHelper.createHttpClient();

        final var id = "user/ 123";

        client.post("/users")
              .putHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
              .rxSendJsonObject(new JsonObject().put(
                  "id", id
              ))
              .blockingGet();

        final var deleteUserResponse = client.delete("/users/" + RibacTestHelper.urlEncode(id))
                                             .putHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                                             .rxSend()
                                             .blockingGet();

        assertEquals(
            SC_NO_CONTENT,
            deleteUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + deleteUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            null,
            deleteUserResponse.bodyAsString()
        );
    }



    @Test
    @Disabled
    void deleteUser_canNotDeleteUnknownUser() {

    }



    @Test
    @Disabled
    void deleteUser_canNotDeleteWithEmptyExternalId() {

    }



    @Test
    @Disabled
    void deleteUser_canNotDeleteWithTooLongExternalId() {

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
}