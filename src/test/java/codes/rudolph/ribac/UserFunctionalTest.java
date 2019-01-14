package codes.rudolph.ribac;

import io.vertx.core.json.JsonObject;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserFunctionalTest {

    @BeforeAll
    static void beforeAll() throws InterruptedException, IOException {
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



    @Test
    void createUser_returnsCreatedUser() {
        final var client = RibacTestHelper.createHttpClient();

        final var id = "guest";

        final var createUserResponse = client.post("/users")
                                             .putHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                                             .rxSendJsonObject(new JsonObject().put(
                                                 "id", id
                                             ))
                                             .blockingGet();
        assertEquals(HttpStatus.SC_CREATED, createUserResponse.statusCode());
        assertEquals(
            ContentType.APPLICATION_JSON.getMimeType(),
            createUserResponse.getHeader(HttpHeaders.CONTENT_TYPE)
        );

        final var expectedCreateUserBody = new JsonObject().put(
            "user", new JsonObject().put(
                "id", id
            )
        );
        assertEquals(expectedCreateUserBody, createUserResponse.bodyAsJsonObject());
    }



    @Test
    @Disabled
    void createUser_canNotCreateUserTwice() {

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