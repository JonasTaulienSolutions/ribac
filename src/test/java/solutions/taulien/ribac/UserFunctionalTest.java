package solutions.taulien.ribac;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.*;
import solutions.taulien.ribac.server.util.FunctionalHelper;

import java.io.IOException;
import java.util.Arrays;

import static org.apache.commons.httpclient.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserFunctionalTest {

    public static final String HEADER_ACCEPT = HttpHeaders.ACCEPT.toString();

    public static final String MIME_APPLICATION_JSON = HttpHeaderValues.APPLICATION_JSON.toString();

    public static final String HEADER_CONTENT_TYPE = HttpHeaders.CONTENT_TYPE.toString();



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
    @DisplayName("createUser_returnsCreatedUser")
    void createUser_returnsCreatedUser(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var id = "user123";

        final var createUserResponse = client.post("/users")
                                             .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                             .putHeader("Request-Id", testInfo.getDisplayName())
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
            MIME_APPLICATION_JSON,
            createUserResponse.getHeader(HEADER_CONTENT_TYPE)
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
    @DisplayName("createUser_canNotCreateUserTwice")
    void createUser_canNotCreateUserTwice(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var id = "user123";

        // 1. Create
        // noinspection ResultOfMethodCallIgnored
        client.post("/users")
              .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
              .putHeader("Request-Id", testInfo.getDisplayName())
              .rxSendJsonObject(new JsonObject().put(
                  "id", id
              ))
              .blockingGet();

        // 2. Try to create again
        final var createUserSecondResponse = client.post("/users")
                                                   .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                                   .putHeader("Request-Id", testInfo.getDisplayName())
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
            MIME_APPLICATION_JSON,
            createUserSecondResponse.getHeader(HEADER_CONTENT_TYPE)
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
    @DisplayName("createUser_canNotCreateUserWithEmptyExternalId")
    void createUser_canNotCreateUserWithEmptyExternalId(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var createUserResponse = client.post("/users")
                                             .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                             .putHeader("Request-Id", testInfo.getDisplayName())
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
            MIME_APPLICATION_JSON,
            createUserResponse.getHeader(HEADER_CONTENT_TYPE)
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
    @DisplayName("createUser_canNotCreateUserWithTooLongExternalId")
    void createUser_canNotCreateUserWithTooLongExternalId(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var id = "" +
                           "user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345." +
                           "user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345." +
                           "user12345.user12345.user12345.user12345.user12345.12345" +
                           "6";

        final var createUserResponse = client.post("/users")
                                             .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                             .putHeader("Request-Id", testInfo.getDisplayName())
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
            MIME_APPLICATION_JSON,
            createUserResponse.getHeader(HEADER_CONTENT_TYPE)
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
    @DisplayName("createUser_canNotCreateUserWithExternalIdAsNumber")
    void createUser_canNotCreateUserWithExternalIdAsNumber(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var createUserResponse = client.post("/users")
                                             .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                             .putHeader("Request-Id", testInfo.getDisplayName())
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
            MIME_APPLICATION_JSON,
            createUserResponse.getHeader(HEADER_CONTENT_TYPE)
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
    @DisplayName("createUser_canNotCreateUserWhenNotProvidingExternalIdField")
    void createUser_canNotCreateUserWhenNotProvidingExternalIdField(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var createUserResponse = client.post("/users")
                                             .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                             .putHeader("Request-Id", testInfo.getDisplayName())
                                             .rxSendJsonObject(new JsonObject())
                                             .blockingGet();
        assertEquals(
            SC_BAD_REQUEST,
            createUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + createUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            MIME_APPLICATION_JSON,
            createUserResponse.getHeader(HEADER_CONTENT_TYPE)
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
    @DisplayName("createUser_canNotCreateUserWhenProvidingEmptyBody")
    void createUser_canNotCreateUserWhenProvidingEmptyBody(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var createUserResponse = client.post("/users")
                                             .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                             .putHeader(HEADER_CONTENT_TYPE, MIME_APPLICATION_JSON)
                                             .putHeader("Request-Id", testInfo.getDisplayName())
                                             .rxSend()
                                             .blockingGet();
        assertEquals(
            SC_BAD_REQUEST,
            createUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + createUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            MIME_APPLICATION_JSON,
            createUserResponse.getHeader(HEADER_CONTENT_TYPE)
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
    @DisplayName("fetchUser_returnsRequestedUser")
    void fetchUser_returnsRequestedUser(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var id = "user/ 123";

        //noinspection ResultOfMethodCallIgnored
        client.post("/users")
              .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
              .putHeader("Request-Id", testInfo.getDisplayName())
              .rxSendJsonObject(new JsonObject().put(
                  "id", id
              ))
              .blockingGet();

        final var fetchUserResponse = client.get("/users/" + RibacTestHelper.urlEncode(id))
                                            .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                            .putHeader("Request-Id", testInfo.getDisplayName())
                                            .rxSend()
                                            .blockingGet();

        assertEquals(
            SC_OK,
            fetchUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + fetchUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            MIME_APPLICATION_JSON,
            fetchUserResponse.getHeader(HEADER_CONTENT_TYPE)
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
    @DisplayName("fetchUser_canNotReturnUnknownUser")
    void fetchUser_canNotReturnUnknownUser(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        var id = "userB";
        final var fetchUserResponse = client.get("/users/" + RibacTestHelper.urlEncode(id))
                                            .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                            .putHeader("Request-Id", testInfo.getDisplayName())
                                            .rxSend()
                                            .blockingGet();

        assertEquals(
            SC_NOT_FOUND,
            fetchUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + fetchUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            MIME_APPLICATION_JSON,
            fetchUserResponse.getHeader(HEADER_CONTENT_TYPE)
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
    @DisplayName("fetchUser_canNotReturnUserWithEmptyExternalId")
    void fetchUser_canNotReturnUserWithEmptyExternalId(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var fetchUserResponse = client.get("/users/")
                                            .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                            .putHeader("Request-Id", testInfo.getDisplayName())
                                            .rxSend()
                                            .blockingGet();

        assertEquals(
            SC_BAD_REQUEST,
            fetchUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + fetchUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            MIME_APPLICATION_JSON,
            fetchUserResponse.getHeader(HEADER_CONTENT_TYPE)
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
    @DisplayName("fetchUser_canNotReturnUserWithTooLongExternalId")
    void fetchUser_canNotReturnUserWithTooLongExternalId(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var id = "" +
                           "user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345." +
                           "user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345." +
                           "user12345.user12345.user12345.user12345.user12345.12345" +
                           "6";
        final var fetchUserResponse = client.get("/users/" + RibacTestHelper.urlEncode(id))
                                            .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                            .putHeader("Request-Id", testInfo.getDisplayName())
                                            .rxSend()
                                            .blockingGet();

        assertEquals(
            SC_BAD_REQUEST,
            fetchUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + fetchUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            MIME_APPLICATION_JSON,
            fetchUserResponse.getHeader(HEADER_CONTENT_TYPE)
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
    @DisplayName("deleteUser_userWasDeleted")
    void deleteUser_userWasDeleted(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var id = "user/ 123";

        //noinspection ResultOfMethodCallIgnored
        client.post("/users")
              .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
              .putHeader("Request-Id", testInfo.getDisplayName())
              .rxSendJsonObject(new JsonObject().put(
                  "id", id
              ))
              .blockingGet();

        final var deleteUserResponse = client.delete("/users/" + RibacTestHelper.urlEncode(id))
                                             .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                             .putHeader("Request-Id", testInfo.getDisplayName())
                                             .rxSend()
                                             .blockingGet();

        assertEquals(
            SC_NO_CONTENT,
            deleteUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + deleteUserResponse.bodyAsString() + "'"
        );
        assertNull(deleteUserResponse.bodyAsString());

        final var getUserResponse = client.get("/users/" + RibacTestHelper.urlEncode(id))
                                          .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                          .putHeader("Request-Id", testInfo.getDisplayName())
                                          .rxSend()
                                          .blockingGet();

        assertEquals(
            SC_NOT_FOUND,
            getUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + deleteUserResponse.bodyAsString() + "'"
        );
    }



    @Test
    @DisplayName("deleteUser_canNotDeleteUnknownUser")
    void deleteUser_canNotDeleteUnknownUser(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var id = "user123";

        final var deleteUserResponse = client.delete("/users/" + RibacTestHelper.urlEncode(id))
                                             .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                             .putHeader("Request-Id", testInfo.getDisplayName())
                                             .rxSend()
                                             .blockingGet();

        assertEquals(
            SC_NOT_FOUND,
            deleteUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + deleteUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            MIME_APPLICATION_JSON,
            deleteUserResponse.getHeader(HEADER_CONTENT_TYPE)
        );
        assertEquals(
            new JsonObject().put(
                "error", new JsonObject().put(
                    "message", "A user with the id '" + id + "' does not exist"
                )
            ),
            deleteUserResponse.bodyAsJsonObject()
        );
    }



    @Test
    @DisplayName("deleteUser_canNotDeleteWithEmptyExternalId")
    void deleteUser_canNotDeleteWithEmptyExternalId(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var deleteUserResponse = client.delete("/users/")
                                             .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                             .putHeader("Request-Id", testInfo.getDisplayName())
                                             .rxSend()
                                             .blockingGet();

        assertEquals(
            SC_BAD_REQUEST,
            deleteUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + deleteUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            MIME_APPLICATION_JSON,
            deleteUserResponse.getHeader(HEADER_CONTENT_TYPE)
        );
        assertEquals(
            new JsonObject().put(
                "error", new JsonObject().put(
                    "message", "Value doesn't respect min length 1"
                )
            ),
            deleteUserResponse.bodyAsJsonObject()
        );
    }



    @Test
    @DisplayName("deleteUser_canNotDeleteWithTooLongExternalId")
    void deleteUser_canNotDeleteWithTooLongExternalId(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();


        final var id = "" +
                           "user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345." +
                           "user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345.user12345." +
                           "user12345.user12345.user12345.user12345.user12345.12345" +
                           "6";
        final var deleteUserResponse = client.delete("/users/" + RibacTestHelper.urlEncode(id))
                                             .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                             .putHeader("Request-Id", testInfo.getDisplayName())
                                             .rxSend()
                                             .blockingGet();

        assertEquals(
            SC_BAD_REQUEST,
            deleteUserResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + deleteUserResponse.bodyAsString() + "'"
        );
        assertEquals(
            MIME_APPLICATION_JSON,
            deleteUserResponse.getHeader(HEADER_CONTENT_TYPE)
        );
        assertEquals(
            new JsonObject().put(
                "error", new JsonObject().put(
                    "message", "Value doesn't respect max length 255"
                )
            ),
            deleteUserResponse.bodyAsJsonObject()
        );
    }



    @Test
    @DisplayName("deleteUser_alsoDeletesEveryGroupMembership")
    @Disabled
    void deleteUser_alsoDeletesEveryGroupMembership(TestInfo testInfo) {

    }



    @Test
    @DisplayName("deleteUser_alsoDeletesEveryUserRight")
    @Disabled
    void deleteUser_alsoDeletesEveryUserRight(TestInfo testInfo) {

    }



    @Test
    @DisplayName("deleteUser_alsoDeletesEveryUserRights")
    @Disabled
    void deleteUser_alsoDeletesEveryUserRights(TestInfo testInfo) {

    }



    @Test
    @DisplayName("fetchUsers_returnsEmptyArrayWhenNoUsersPresent")
    void fetchUsers_returnsEmptyArrayWhenNoUsersPresent(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var fetchUsersResponse = client.get("/users")
                                             .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                             .putHeader("Request-Id", testInfo.getDisplayName())
                                             .rxSend()
                                             .blockingGet();

        assertEquals(
            SC_OK,
            fetchUsersResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + fetchUsersResponse.bodyAsString() + "'"
        );
        assertEquals(
            MIME_APPLICATION_JSON,
            fetchUsersResponse.getHeader(HEADER_CONTENT_TYPE)
        );
        assertEquals(
            new JsonObject().put(
                "allUsers", new JsonArray()
            ),
            fetchUsersResponse.bodyAsJsonObject()
        );
    }



    @Test
    @DisplayName("fetchUsers_returnsAllCreatedUsers")
    void fetchUsers_returnsAllCreatedUsers(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var userIds = Arrays.asList("user1", "user2", "user3", "user4");
        //noinspection ResultOfMethodCallIgnored
        userIds.forEach(
            userId -> client.post("/users")
                          .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                          .putHeader("Request-Id", testInfo.getDisplayName())
                          .rxSendJsonObject(new JsonObject().put(
                              "id", userId
                          ))
                          .blockingGet()
        );

        final var fetchUsersResponse = client.get("/users")
                                             .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                             .putHeader("Request-Id", testInfo.getDisplayName())
                                             .rxSend()
                                             .blockingGet();

        assertEquals(
            SC_OK,
            fetchUsersResponse.statusCode(),
            () -> "Unexpected status code. Response body: '" + fetchUsersResponse.bodyAsString() + "'"
        );
        assertEquals(
            MIME_APPLICATION_JSON,
            fetchUsersResponse.getHeader(HEADER_CONTENT_TYPE)
        );
        assertEquals(
            new JsonObject().put(
                "allUsers", new JsonArray(
                    FunctionalHelper.mapAll(userIds, userId -> new JsonObject().put("id", userId))
                )
            ),
            fetchUsersResponse.bodyAsJsonObject()
        );
    }
}