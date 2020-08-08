package solutions.taulien.ribac;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.*;
import solutions.taulien.ribac.server.util.FunctionalHelper;

import java.io.IOException;
import java.util.Arrays;

import static org.apache.commons.httpclient.HttpStatus.*;

public class GroupFunctionalTest {

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
    @DisplayName("createGroup_returnsCreatedGroup")
    void createGroup_returnsCreatedGroup(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var name = "group123";

        final var createGroupResponse = RibacTestHelper.post(
            client,
            testInfo,
            "/groups",
            new JsonObject().put("name", name)
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            createGroupResponse,
            SC_CREATED,
            new JsonObject().put(
                "createdGroup", new JsonObject().put(
                    "name", name
                )
            )
        );
    }



    @Test
    @DisplayName("createGroup_canNotCreateGroupTwice")
    void createGroup_canNotCreateGroupTwice(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var name = "group123";

        // 1. Create
        RibacTestHelper.post(
            client,
            testInfo,
            "/groups",
            new JsonObject().put("name", name)
        );

        // 2. Try to create again
        final var createGroupSecondResponse = RibacTestHelper.post(
            client,
            testInfo,
            "/groups",
            new JsonObject().put("name", name)
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            createGroupSecondResponse,
            SC_CONFLICT,
            RibacTestHelper.createErrorResponseBody("A group already exists with the name '" + name + "'")
        );
    }



    @Test
    @DisplayName("createGroup_canNotCreateGroupWithEmptyName")
    void createGroup_canNotCreateGroupWithEmptyName(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var createGroupResponse = RibacTestHelper.post(
            client,
            testInfo,
            "/groups",
            new JsonObject().put("name", "")
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            createGroupResponse,
            SC_BAD_REQUEST,
            RibacTestHelper.createErrorResponseBody("$.name: must be at least 1 characters long")
        );
    }



    @Test
    @DisplayName("createGroup_canNotCreateGroupWithTooLongName")
    void createGroup_canNotCreateGroupWithTooLongName(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var name = ""
                             + "group1234.group1234.group1234.group1234.group1234.group1234.group1234.group1234.group1234.group1234."
                             + "group1234.group1234.group1234.group1234.group1234.group1234.group1234.group1234.group1234.group1234."
                             + "group1234.group1234.group1234.group1234.group1234.12345"
                             + "6";

        final var createGroupResponse = RibacTestHelper.post(
            client,
            testInfo,
            "/groups",
            new JsonObject().put("name", name)
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            createGroupResponse,
            SC_BAD_REQUEST,
            RibacTestHelper.createErrorResponseBody("$.name: may only be 255 characters long")
        );
    }



    @Test
    @DisplayName("createGroup_canNotCreateGroupWithNameAsNumber")
    void createGroup_canNotCreateGroupWithNameAsNumber(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var createGroupResponse = RibacTestHelper.post(
            client,
            testInfo,
            "/groups",
            new JsonObject().put("name", 1234)
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            createGroupResponse,
            SC_BAD_REQUEST,
            RibacTestHelper.createErrorResponseBody("$.name: integer found, string expected")
        );
    }



    @Test
    @DisplayName("createGroup_canNotCreateGroupWhenNotProvidingNameField")
    void createGroup_canNotCreateGroupWhenNotProvidingNameField(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var createGroupResponse = RibacTestHelper.post(
            client,
            testInfo,
            "/groups",
            new JsonObject()
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            createGroupResponse,
            SC_BAD_REQUEST,
            RibacTestHelper.createErrorResponseBody("$.name: is missing but it is required")
        );
    }



    @Test
    @DisplayName("createGroup_canNotCreateUserWhenProvidingEmptyBody")
    void createGroup_canNotCreateUserWhenProvidingEmptyBody(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var createGroupResponse = RibacTestHelper.postWithoutBody(
            client,
            testInfo,
            "/groups"
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            createGroupResponse,
            SC_BAD_REQUEST,
            RibacTestHelper.createErrorResponseBody("Bad Request")
        );
    }

    @Test
    @DisplayName("fetchGroups_returnsEmptyArrayWhenNoGroupIsPresent")
    void fetchGroups_returnsEmptyArrayWhenNoGroupIsPresent(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var fetchGroupsResponse = RibacTestHelper.get(
            client,
            testInfo,
            "/groups"
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            fetchGroupsResponse,
            SC_OK,
            new JsonObject().put(
                "allGroups", new JsonArray()
            )
        );
    }



    @Test
    @DisplayName("fetchGroups_returnsAllCreatedGroups")
    void fetchGroups_returnsAllCreatedGroups(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var groupNames = Arrays.asList("group1", "group2", "group3", "group4");
        groupNames.forEach(
            groupName -> RibacTestHelper.post(
                client,
                testInfo,
                "/groups",
                new JsonObject().put("name", groupName)
            )
        );

        final var fetchGroupsResponse = RibacTestHelper.get(
            client,
            testInfo,
            "/groups"
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            fetchGroupsResponse,
            SC_OK,
            new JsonObject().put(
                "allGroups", new JsonArray(
                    FunctionalHelper.mapAll(groupNames, userId -> new JsonObject().put("name", userId))
                )
            )
        );
    }
}
