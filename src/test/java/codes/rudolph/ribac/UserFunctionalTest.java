package codes.rudolph.ribac;

import io.vertx.core.json.JsonObject;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
            HttpStatus.SC_CREATED,
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
            HttpStatus.SC_CONFLICT,
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
            HttpStatus.SC_BAD_REQUEST,
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
            HttpStatus.SC_BAD_REQUEST,
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
            HttpStatus.SC_BAD_REQUEST,
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
            HttpStatus.SC_BAD_REQUEST,
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
            HttpStatus.SC_BAD_REQUEST,
            createUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + createUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            ContentType.APPLICATION_JSON.getMimeType(),
            createUserResponse.getHeader(HttpHeaders.CONTENT_TYPE)
        );
        //TODO: OpenApiValidationFailureHandler must be altered so it does not respond with 404 in this case
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

        final var fetchUserResponse = client.get("/users/" + URLEncoder.encode(id, StandardCharsets.UTF_8).replace("+", "%20"))
                                            .putHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                                            .rxSend()
                                            .blockingGet();

        assertEquals(
            HttpStatus.SC_OK,
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