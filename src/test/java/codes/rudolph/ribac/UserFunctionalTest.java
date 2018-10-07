package codes.rudolph.ribac;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserFunctionalTest {

    @BeforeAll
    static void init() throws InterruptedException, IOException {
        var process = Runtime.getRuntime().exec("docker-compose rm --stop --force ribac-db");
        new BufferedReader(new InputStreamReader(process.getInputStream())).lines().forEach(System.out::println);
        new BufferedReader(new InputStreamReader(process.getErrorStream())).lines().forEach(System.out::println);
        process.waitFor();
    }



    @BeforeEach
    void dbUp() throws IOException, InterruptedException {
        var process = Runtime.getRuntime().exec("docker-compose up --detach ribac-db");
        new BufferedReader(new InputStreamReader(process.getInputStream())).lines().forEach(System.out::println);
        new BufferedReader(new InputStreamReader(process.getErrorStream())).lines().forEach(System.out::println);
        process.waitFor();

        Thread.sleep(10000);
    }



    @AfterEach
    void dbDown() throws IOException, InterruptedException {
        var process = Runtime.getRuntime().exec("docker-compose rm --stop --force ribac-db");
        new BufferedReader(new InputStreamReader(process.getInputStream())).lines().forEach(System.out::println);
        new BufferedReader(new InputStreamReader(process.getErrorStream())).lines().forEach(System.out::println);
        process.waitFor();
    }



    @Test
    void createUser_userWasCreated() {
        final var client = createHttpClient();

        final var externalId = "guest";

        // Create user
        final var createUserResponse = client.post("/users")
                                             .rxSendJsonObject(new JsonObject().put("externalId", externalId))
                                             .blockingGet();
        assertEquals(HttpStatus.SC_CREATED, createUserResponse.statusCode());

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
    void createUser_canNotCreateUserWhenNotProvidingExternalIdField() {

    }



    @Test
    @Disabled
    void createUser_canNotCreateUserWhenProvidingEmptyBody() {

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



    private static WebClient createHttpClient() {
        final var options = new WebClientOptions().setDefaultHost("localhost")
                                                  .setDefaultPort(8080);

        return WebClient.create(Vertx.vertx(), options);
    }
}