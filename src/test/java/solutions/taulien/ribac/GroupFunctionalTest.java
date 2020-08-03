package solutions.taulien.ribac;

import org.junit.jupiter.api.*;

import java.io.IOException;

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
    @Disabled
    void createGroup_returnsCreatedGroup(TestInfo testInfo) {

    }
    @Test
    @DisplayName("createGroup_canNotCreateGroupTwice")
    @Disabled
    void createGroup_canNotCreateGroupTwice(TestInfo testInfo) {

    }
    @Test
    @DisplayName("createGroup_canNotCreateGroupWithEmptyName")
    @Disabled
    void createGroup_canNotCreateGroupWithEmptyName(TestInfo testInfo) {

    }
    @Test
    @DisplayName("createGroup_canNotCreateGroupWithTooLongName")
    @Disabled
    void createGroup_canNotCreateGroupWithTooLongName(TestInfo testInfo) {

    }
    @Test
    @DisplayName("createGroup_canNotCreateGroupWithNameAsNumber")
    @Disabled
    void createGroup_canNotCreateGroupWithNameAsNumber(TestInfo testInfo) {

    }
    @Test
    @DisplayName("createGroup_canNotCreateGroupWhenNotProvidingNameField")
    @Disabled
    void createGroup_canNotCreateGroupWhenNotProvidingNameField(TestInfo testInfo) {

    }
    @Test
    @DisplayName("createGroup_canNotCreateUserWhenProvidingEmptyBody")
    @Disabled
    void createGroup_canNotCreateUserWhenProvidingEmptyBody(TestInfo testInfo) {

    }
}
