package solutions.taulien.ribac;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.*;
import solutions.taulien.ribac.server.util.FunctionalHelper;

import java.io.IOException;
import java.util.Arrays;

import static org.apache.commons.httpclient.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.assertNull;

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
            RibacTestHelper.createErrorResponseBody(testInfo, "A Group already exists with the name '" + name + "'")
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
            RibacTestHelper.createErrorResponseBody(testInfo, "$.name: must be at least 1 characters long")
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
            RibacTestHelper.createErrorResponseBody(testInfo, "$.name: may only be 255 characters long")
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
            RibacTestHelper.createErrorResponseBody(testInfo, "$.name: integer found, string expected")
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
            RibacTestHelper.createErrorResponseBody(testInfo, "$.name: is missing but it is required")
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
            RibacTestHelper.createErrorResponseBody(testInfo, "Bad Request")
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
                    FunctionalHelper.mapAll(groupNames, groupName -> new JsonObject().put("name", groupName))
                )
            )
        );
    }



    @Test
    @DisplayName("deleteGroup_groupWasDeleted")
    void deleteGroup_groupWasDeleted(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var name = "group/ 123";

        RibacTestHelper.post(
            client,
            testInfo,
            "/groups",
            new JsonObject().put("name", name)
        );

        final var deleteGroupResponse = RibacTestHelper.delete(
            client,
            testInfo,
            "/groups/" + RibacTestHelper.urlEncode(name)
        );

        RibacTestHelper.assetStatusCodeEquals(deleteGroupResponse, SC_NO_CONTENT);
        assertNull(deleteGroupResponse.bodyAsString());

        final var getGroupResponse = RibacTestHelper.get(
            client,
            testInfo,
            "/group/" + RibacTestHelper.urlEncode(name)
        );

        RibacTestHelper.assetStatusCodeEquals(getGroupResponse, SC_NOT_FOUND);
    }



    @Test
    @DisplayName("deleteGroup_canNotDeleteUnknownGroup")
    void deleteGroup_canNotDeleteUnknownGroup(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var name = "group123";

        final var deleteGroupResponse = RibacTestHelper.delete(
            client,
            testInfo,
            "/groups/" + RibacTestHelper.urlEncode(name)
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            deleteGroupResponse,
            SC_NOT_FOUND,
            RibacTestHelper.createErrorResponseBody(testInfo, "A Group with the name '" + name + "' does not exist")
        );
    }



    @Test
    @DisplayName("deleteGroup_canNotDeleteWithEmptyName")
    void deleteGroup_canNotDeleteWithEmptyName(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var deleteGroupResponse = RibacTestHelper.delete(
            client,
            testInfo,
            "/groups/"
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            deleteGroupResponse,
            SC_BAD_REQUEST,
            RibacTestHelper.createErrorResponseBody(testInfo, "Value doesn't respect min length 1")
        );
    }



    @Test
    @DisplayName("deleteGroup_canNotDeleteWithTooLongName")
    void deleteGroup_canNotDeleteWithTooLongName(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var name = ""
                             + "group1234.group1234.group1234.group1234.group1234.group1234.group1234.group1234.group1234.group1234."
                             + "group1234.group1234.group1234.group1234.group1234.group1234.group1234.group1234.group1234.group1234."
                             + "group1234.group1234.group1234.group1234.group1234.12345"
                             + "6";

        final var deleteGroupResponse = RibacTestHelper.delete(
            client,
            testInfo,
            "/groups/" + RibacTestHelper.urlEncode(name)
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            deleteGroupResponse,
            SC_BAD_REQUEST,
            RibacTestHelper.createErrorResponseBody(testInfo, "Value doesn't respect max length 255")
        );
    }



    @Test
    @DisplayName("deleteGroup_alsoDeletesEveryGroupMembership")
    @Disabled
    void deleteGroup_alsoDeletesEveryGroupMembership(TestInfo testInfo) {
        // TODO: Implement
    }



    @Test
    @DisplayName("deleteGroup_alsoDeletesEveryGroupRight")
    @Disabled
    void deleteGroup_alsoDeletesEveryGroupRight(TestInfo testInfo) {
        // TODO: Implement
    }



    @Test
    @DisplayName("deleteGroup_alsoDeletesEveryGroupRightSet")
    @Disabled
    void deleteGroup_alsoDeletesEveryGroupRightSet(TestInfo testInfo) {
        // TODO: Implement
    }



    @Test
    @DisplayName("fetchGroup_returnsRequestedGroup")
    void fetchGroup_returnsRequestedGroup(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var name = "group/ 123";

        RibacTestHelper.post(
            client,
            testInfo,
            "/groups",
            new JsonObject().put("name", name)
        );

        final var fetchGroupResponse = RibacTestHelper.get(
            client,
            testInfo,
            "/groups/" + RibacTestHelper.urlEncode(name)
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            fetchGroupResponse,
            SC_OK,
            new JsonObject().put(
                "requestedGroup", new JsonObject().put(
                    "name", name
                )
            )
        );
    }



    @Test
    @DisplayName("fetchGroup_canNotReturnUnknownGroup")
    void fetchGroup_canNotReturnUnknownGroup(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        var name = "groupB";
        final var fetchGroupResponse = RibacTestHelper.get(
            client,
            testInfo,
            "/groups/" + RibacTestHelper.urlEncode(name)
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            fetchGroupResponse,
            SC_NOT_FOUND,
            RibacTestHelper.createErrorResponseBody(testInfo, "A Group with the name '" + name + "' does not exist")
        );
    }



    @Test
    @DisplayName("fetchGroup_canNotReturnGroupWithEmptyName")
    void fetchGroup_canNotReturnGroupWithEmptyName(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var fetchGroupResponse = RibacTestHelper.get(
            client,
            testInfo,
            "/groups/"
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            fetchGroupResponse,
            SC_BAD_REQUEST,
            RibacTestHelper.createErrorResponseBody(testInfo, "Value doesn't respect min length 1")
        );
    }



    @Test
    @DisplayName("fetchUGroup_canNotReturnGroupWithTooLongName")
    void fetchUGroup_canNotReturnGroupWithTooLongName(TestInfo testInfo) {
        final var client = RibacTestHelper.createHttpClient();

        final var name = ""
                             + "group1234.group1234.group1234.group1234.group1234.group1234.group1234.group1234.group1234.group1234."
                             + "group1234.group1234.group1234.group1234.group1234.group1234.group1234.group1234.group1234.group1234."
                             + "group1234.group1234.group1234.group1234.group1234.12345"
                             + "6";
        final var fetchGroupResponse = RibacTestHelper.get(
            client,
            testInfo,
            "/groups/" + RibacTestHelper.urlEncode(name)
        );

        RibacTestHelper.assertStatusCodeAndBodyEquals(
            fetchGroupResponse,
            SC_BAD_REQUEST,
            RibacTestHelper.createErrorResponseBody(testInfo, "Value doesn't respect max length 255")
        );
    }
}
