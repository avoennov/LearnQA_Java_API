package exercises;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Ex11RequestCookie {

    @Test
    /*
    Необходимо написать тест, который делает запрос на метод: https://playground.learnqa.ru/api/homework_cookie
    Этот метод возвращает какую-то cookie с каким-то значением. Необходимо понять что за cookie и с каким значением, и зафиксировать это поведение с помощью assert.
    Результатом должна быть ссылка на коммит с тестом.
    */

    public void testRequestCookie(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();
        response.print();

        Map<String,String> cookie = response.getCookies();
        System.out.println(cookie);
        assertEquals(200, response.statusCode(), "Unexpected status code");
        assertTrue(cookie.containsKey("HomeWork"), "Response doesn't have 'HomeWork' cookie");
        assertTrue(cookie.containsValue("hw_value"), "Cookie doesn't have 'hw_value' value");
    }
}
