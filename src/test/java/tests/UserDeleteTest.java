package tests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private final String userUrl = "https://playground.learnqa.ru/api/user/";
    private final String loginUrl = "https://playground.learnqa.ru/api/user/login";


    //-----------[Ex18: Тесты на DELETE]--------------------
    //Удалить пользователя по ID 2. Убедиться, что система не даст вам удалить этого пользователя.
    @Test
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
