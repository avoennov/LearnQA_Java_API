package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private final String userUrl = "https://playground.learnqa.ru/api/user/";
    private final String loginUrl = "https://playground.learnqa.ru/api/user/login";

    @Test
    public void testEditJustCreatedTest() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        Assertions.asserJsonByName(responseUserData, "firstName", newName);
    }

    //-----------[Ex17: ???????????????????? ?????????? ???? PUT]--------------------
    //???????????????????? ???????????????? ???????????? ????????????????????????, ???????????? ??????????????????????????????????
    @Test
    public void testEditNotAuthorizedUser() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = new ApiCoreRequests()
                .makePostRequest(userUrl, userData).jsonPath();
        String userId = responseCreateAuth.getString("id");
        String userIdUrl = userUrl + userId;

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequest(userIdUrl, editData);

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(loginUrl, authData);

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        userIdUrl,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.asserJsonByName(responseUserData, "firstName", "learnqa");
    }

    //???????????????????? ???????????????? ???????????? ????????????????????????, ???????????? ?????????????????????????????? ???????????? ??????????????????????????
    @Test
    public void testEditUserAuthAsAnotherUser() {
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

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequest(
                        userIdUrl,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData);

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

    //???????????????????? ???????????????? email ????????????????????????, ???????????? ?????????????????????????????? ?????? ???? ??????????????????????????, ???? ?????????? email ?????? ?????????????? @
    @Test
    public void testEditEmailToWrong() {
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

        //EDIT
        Map<String, String> editData = new HashMap<>();
        editData.put("email", "wrongmail.com");

        Response responseEditUser = apiCoreRequests
                .makePutRequest(
                        userIdUrl,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData);

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        userIdUrl,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.asserJsonByName(responseUserData, "email", userData.get("email"));
    }

    //???????????????????? ???????????????? firstName ????????????????????????, ???????????? ?????????????????????????????? ?????? ???? ??????????????????????????, ???? ?????????? ???????????????? ???????????????? ?? ???????? ????????????
    @Test
    public void testEditNameToWrong() {
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

        //EDIT
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "a");

        Response responseEditUser = apiCoreRequests
                .makePutRequest(
                        userIdUrl,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData);

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        userIdUrl,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.asserJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }
}
