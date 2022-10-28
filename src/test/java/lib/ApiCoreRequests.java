package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {
    @Step("Make a GET-request with token and auth cookie")
    public Response makeGetRequest(String url, String token, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with auth cookie only")
    public Response makeGetRequestWithCookie(String url, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with token only")
    public Response makeGetRequestWithToken(String url, String token){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();
    }

    @Step("Make a POST-request")
    public Response makePostRequest(String url, Map<String, String> authData){
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .andReturn();
    }

    @Step("Make a POST-request without required field")
    public Response makePostRequestWithoutRequiredField(String url, String fieldName){
        Map<String,String> userData = new HashMap<>();
        userData.put(fieldName, null);
        userData = DataGenerator.getRegistrationData(userData);

        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step("Make a PUT-request with token and auth cookie")
    public Response makePutRequest(String url, String token, String cookie, Map<String,String> data){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token",token))
                .cookie("auth_sid",cookie)
                .body(data)
                .put(url)
                .andReturn();
    }

    @Step("Make a PUT-request")
    public Response makePutRequest(String url, Map<String,String> data){
        return given()
                .filter(new AllureRestAssured())
                .body(data)
                .put(url)
                .andReturn();
    }

    @Step("Make a DELETE-request")
    public Response makeDeleteRequest(String url, String token, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token",token))
                .cookie("auth_sid",cookie)
                .delete(url)
                .andReturn();
    }
}
