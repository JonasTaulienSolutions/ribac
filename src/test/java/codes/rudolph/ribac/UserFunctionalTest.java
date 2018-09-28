package codes.rudolph.ribac;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserFunctionalTest {

    @BeforeEach
    void startRibac() {

    }



    @AfterEach
    void stopRibac() {

    }



    @Test
    void createUser_userWasCreated() {
        final var client = createHttpClient();

        final var externalId = "guest";

        // Create User
        final var createUserResponse = client.post("/users")
                                             .rxSendJsonObject(new JsonObject().put("externalId", externalId))
                                             .blockingGet();
        assertEquals(HttpStatus.SC_OK, createUserResponse.statusCode());

        final var createUserBody = createUserResponse.bodyAsJsonObject();
        final var createdUser = createUserBody.getJsonObject("user");
        final int createdUserInternalId = createdUser.getInteger("id");
        final var createdUserExternalId = createdUser.getString("externalId");

        assertEquals(1, createdUserInternalId);
        assertEquals(externalId, createdUserExternalId);

        // Fetch created user
        final var fetchCreatedUserResponse = client.get("/users/" + createdUserInternalId).rxSend().blockingGet();
        assertEquals(HttpStatus.SC_OK, fetchCreatedUserResponse.statusCode());

        final var fetchedUserBody = fetchCreatedUserResponse.bodyAsJsonObject();
        final var fetchedUser = fetchedUserBody.getJsonObject("user");
        final int fetchedUserInternalId = fetchedUser.getInteger("id");
        final var fetchedUserExternalId = fetchedUser.getString("externalId");

        assertEquals(1, fetchedUserInternalId);
        assertEquals(externalId, fetchedUserExternalId);
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



    private static WebClient createHttpClient() {
        final var options = new WebClientOptions().setDefaultHost("localhost")
                                                  .setDefaultPort(8080);

        return WebClient.create(Vertx.vertx(), options);
    }
}