package solutions.taulien.ribac;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.*;
import solutions.taulien.ribac.server.util.FunctionalHelper;

import java.io.IOException;
import java.util.Arrays;

import static org.apache.commons.httpclient.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static solutions.taulien.ribac.RibacTestHelper.HEADER_ACCEPT;
import static solutions.taulien.ribac.RibacTestHelper.MIME_APPLICATION_JSON;

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
    @DisplayName("createUser_returnsCreatedUser")
    void createUser_returnsCreatedUser(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var id = "user123";

        final var createUserResponse = RibacTestHelper.post(
            client,
            testInfo,
            "/users",
            new JsonObject().put("id", id)
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            createUserResponse,
            SC_CREATED,
            new JsonObject().put(
                "createdUser", new JsonObject().put(
                    "id", id
                )
            )
        );
    }



    @Test
    @DisplayName("createUser_canNotCreateUserTwice")
    void createUser_canNotCreateUserTwice(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var id = "user123";

        // 1. Create
        RibacTestHelper.post(
            client,
            testInfo,
            "/users",
            new JsonObject().put("id", id)
        );

        // 2. Try to create again
        final var createUserSecondResponse = RibacTestHelper.post(
            client,
            testInfo,
            "/users",
            new JsonObject().put("id", id)
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            createUserSecondResponse,
            SC_CONFLICT,
            RibacTestHelper.createErrorResponseBody("A user already exists with the id '" + id + "'")
        );
    }



    @Test
    @DisplayName("createUser_canNotCreateUserWithEmptyExternalId")
    void createUser_canNotCreateUserWithEmptyExternalId(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var createUserResponse = RibacTestHelper.post(
            client,
            testInfo,
            "/users",
            new JsonObject().put("id", "")
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            createUserResponse,
            SC_BAD_REQUEST,
            RibacTestHelper.createErrorResponseBody("$.id: must be at least 1 characters long")
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

        final var createUserResponse = RibacTestHelper.post(
            client,
            testInfo,
            "/users",
            new JsonObject().put("id", id)
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            createUserResponse,
            SC_BAD_REQUEST,
            RibacTestHelper.createErrorResponseBody("$.id: may only be 255 characters long")
        );
    }



    @Test
    @DisplayName("createUser_canNotCreateUserWithExternalIdAsNumber")
    void createUser_canNotCreateUserWithExternalIdAsNumber(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var createUserResponse = RibacTestHelper.post(
            client,
            testInfo,
            "/users",
            new JsonObject().put("id", 1234)
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            createUserResponse,
            SC_BAD_REQUEST,
            RibacTestHelper.createErrorResponseBody("$.id: integer found, string expected")
        );
    }



    @Test
    @DisplayName("createUser_canNotCreateUserWhenNotProvidingExternalIdField")
    void createUser_canNotCreateUserWhenNotProvidingExternalIdField(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var createUserResponse = RibacTestHelper.post(
            client,
            testInfo,
            "/users",
            new JsonObject()
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            createUserResponse,
            SC_BAD_REQUEST,
            RibacTestHelper.createErrorResponseBody("$.id: is missing but it is required")
        );
    }



    @Test
    @DisplayName("createUser_canNotCreateUserWhenProvidingEmptyBody")
    void createUser_canNotCreateUserWhenProvidingEmptyBody(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var createUserResponse = RibacTestHelper.postWithoutBody(
            client,
            testInfo,
            "/users"
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            createUserResponse,
            SC_BAD_REQUEST,
            RibacTestHelper.createErrorResponseBody("Bad Request")
        );
    }



    @Test
    @DisplayName("fetchUser_returnsRequestedUser")
    void fetchUser_returnsRequestedUser(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var id = "user/ 123";

        RibacTestHelper.post(
            client,
            testInfo,
            "/users",
            new JsonObject().put("id", id)
        );

        final var fetchUserResponse = client.get("/users/" + RibacTestHelper.urlEncode(id))
                                            .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                            .putHeader("Request-Id", testInfo.getDisplayName())
                                            .rxSend()
                                            .blockingGet();

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            fetchUserResponse,
            SC_OK,
            new JsonObject().put(
                "requestedUser", new JsonObject().put(
                    "id", id
                )
            )
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

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            fetchUserResponse,
            SC_NOT_FOUND,
            RibacTestHelper.createErrorResponseBody("A user with the id '" + id + "' does not exist")
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

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            fetchUserResponse,
            SC_BAD_REQUEST,
            RibacTestHelper.createErrorResponseBody("Value doesn't respect min length 1")
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

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            fetchUserResponse,
            SC_BAD_REQUEST,
            RibacTestHelper.createErrorResponseBody("Value doesn't respect max length 255")
        );
    }



    @Test
    @DisplayName("deleteUser_userWasDeleted")
    void deleteUser_userWasDeleted(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var id = "user/ 123";

        RibacTestHelper.post(
            client,
            testInfo,
            "/users",
            new JsonObject().put("id", id)
        );

        final var deleteUserResponse = client.delete("/users/" + RibacTestHelper.urlEncode(id))
                                             .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                             .putHeader("Request-Id", testInfo.getDisplayName())
                                             .rxSend()
                                             .blockingGet();

        RibacTestHelper.assetStatusCodeEquals(deleteUserResponse, SC_NO_CONTENT);
        assertNull(deleteUserResponse.bodyAsString());

        final var getUserResponse = client.get("/users/" + RibacTestHelper.urlEncode(id))
                                          .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                          .putHeader("Request-Id", testInfo.getDisplayName())
                                          .rxSend()
                                          .blockingGet();

        RibacTestHelper.assetStatusCodeEquals(getUserResponse, SC_NOT_FOUND);
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

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            deleteUserResponse,
            SC_NOT_FOUND,
            RibacTestHelper.createErrorResponseBody("A user with the id '" + id + "' does not exist")
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

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            deleteUserResponse,
            SC_BAD_REQUEST,
            RibacTestHelper.createErrorResponseBody("Value doesn't respect min length 1")
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

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            deleteUserResponse,
            SC_BAD_REQUEST,
            RibacTestHelper.createErrorResponseBody("Value doesn't respect max length 255")
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

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            fetchUsersResponse,
            SC_OK,
            new JsonObject().put(
                "allUsers", new JsonArray()
            )
        );
    }



    @Test
    @DisplayName("fetchUsers_returnsAllCreatedUsers")
    void fetchUsers_returnsAllCreatedUsers(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var userIds = Arrays.asList("user1", "user2", "user3", "user4");
        userIds.forEach(
            userId -> RibacTestHelper.post(
                client,
                testInfo,
                "/users",
                new JsonObject().put("id", userId)
            )
        );

        final var fetchUsersResponse = client.get("/users")
                                             .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                                             .putHeader("Request-Id", testInfo.getDisplayName())
                                             .rxSend()
                                             .blockingGet();

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            fetchUsersResponse,
            SC_OK,
            new JsonObject().put(
                "allUsers", new JsonArray(
                    FunctionalHelper.mapAll(userIds, userId -> new JsonObject().put("id", userId))
                )
            )
        );
    }
}