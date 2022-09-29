package exercises;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Ex12RequestHeader {

    @Test
    /*
    Необходимо написать тест, который делает запрос на метод: https://playground.learnqa.ru/api/homework_header
    Этот метод возвращает headers с каким-то значением. Необходимо понять что за headers и с каким значением, и зафиксировать это поведение с помощью assert
    Результатом должна быть ссылка на коммит с тестом.
    */

    public void testRequestHeader(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();
        response.print();

        Headers headers = response.getHeaders();
        System.out.println(headers);

        assertTrue(headers.hasHeaderWithName("x-secret-homework-header"), "Response doesn't have 'x-secret-homework-header' header");

        String headerValue = headers.getValue("x-secret-homework-header");
        assertEquals("Some secret value", headerValue, "Header doesn't have 'Some secret value' value");
    }
}
