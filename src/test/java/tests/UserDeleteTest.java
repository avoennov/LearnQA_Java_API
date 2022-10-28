package tests;

import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Delete User cases")
@Feature("Delete")
public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private final String userUrl = "https://playground.learnqa.ru/api/user/";
    private final String loginUrl = "https://playground.learnqa.ru/api/user/login";


    //-----------[Ex18: Тесты на DELETE]--------------------
    //Удалить пользователя по ID 2. Убедиться, что система не даст вам удалить этого пользователя.
    @Test
    @Description("[Negative test] Delete user by ID 2. Make sure that the system will not let you delete this user.")
    @DisplayName("Delete not removable User")
    @TmsLink("<link to related test case in test management tool>")
    @Story("Implement deleting Users")
    @Tags({@Tag("backend"), @Tag("smoke")})
    @Severity(SeverityLevel.BLOCKER)

    public void testDeleteNonRemovableUser() {
        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(loginUrl, authData);

        //DELETE
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(
                        userUrl + "2",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));
        responseDeleteUser.print();
        Assertions.assertResponseTextEquals(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        userUrl + "2",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.asserJsonByName(responseUserData, "firstName", "Vitalii");
    }

    //Создать пользователя, авторизоваться из-под него, удалить, затем попробовать получить его данные по ID и убедиться, что пользователь действительно удален.
    @Test
    @Description("[Positive test] Create a user, log in, delete, then try to get his data by ID and make sure that the user is really deleted.")
    @DisplayName("Delete removable User")
    @TmsLink("<link to related test case in test management tool>")
    @Story("Implement deleting Users")
    @Tags({@Tag("backend"), @Tag("smoke")})
    @Severity(SeverityLevel.CRITICAL)

    public void testDeleteRemovableUser() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = new ApiCoreRequests()
                .makePostRequest(userUrl, userData).jsonPath();
        String userId = responseCreateAuth.getString("id");
        String userIdUrl = userUrl + userId;

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(loginUrl, authData);

        //DELETE
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(
                        userIdUrl,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        userIdUrl,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertResponseTextEquals(responseUserData, "User not found");
    }

    //Попробовать удалить пользователя, будучи авторизованными другим пользователем
    @Test
    @Description("[Negative test] Try to delete a user while logged in by another user")
    @DisplayName("Delete User authorized as another User")
    @TmsLink("<link to related test case in test management tool>")
    @Story("Implement deleting Users")
    @Tags({@Tag("backend"), @Tag("regression")})
    @Severity(SeverityLevel.BLOCKER)

    public void testDeleteUserAuthAsAnotherUser() {
        //GENERATE USER (Main)
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = new ApiCoreRequests()
                .makePostRequest(userUrl, userData).jsonPath();

        String userId = responseCreateAuth.getString("id");
        String userIdUrl = userUrl + userId;

        //LOGIN as Another User
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(loginUrl, authData);

        //DELETE
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(
                        userIdUrl,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        //LOGIN as Main User
        Map<String, String> authDataMainUser = new HashMap<>();
        authDataMainUser.put("email", userData.get("email"));
        authDataMainUser.put("password", userData.get("password"));

        Response responseGetAuthMainUser = apiCoreRequests
                .makePostRequest(loginUrl, authDataMainUser);

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        userIdUrl,
                        this.getHeader(responseGetAuthMainUser, "x-csrf-token"),
                        this.getCookie(responseGetAuthMainUser, "auth_sid"));

        Assertions.asserJsonByName(responseUserData, "firstName", "learnqa");
    }
}
