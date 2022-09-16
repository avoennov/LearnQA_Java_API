package exercises;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


public class Ex7LongRedirect {

    @Test
    /*
    Необходимо написать тест, который создает GET-запрос на адрес из предыдущего задания: https://playground.learnqa.ru/api/long_redirect
    На самом деле этот URL ведет на другой, который мы должны были узнать на предыдущем занятии. Но этот другой URL тоже куда-то редиректит.
    И так далее. Мы не знаем заранее количество всех редиректов и итоговый адрес.
    Наша задача — написать цикл, который будет создавать запросы в цикле, каждый раз читая URL для редиректа из нужного заголовка.
    И так, пока мы не дойдем до ответа с кодом 200.
    Ответом должна быть ссылка на тест в вашем репозитории и количество редиректов.
    */

    public void testLongRedirect(){

        String url = "https://playground.learnqa.ru/api/long_redirect";
        int redirectsCount = 0;
        int statusCode;

        do {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(url)
                    .andReturn();

            statusCode = response.getStatusCode();
            System.out.println(statusCode);

            if(statusCode != 200) {
                url = response.getHeader("Location");
                System.out.println(url);
            } else {
                String host = response.getHeader("X-Host");
                System.out.println(host);
            }
            redirectsCount++;

        } while (statusCode != 200);
        System.out.println("Count of redirects: " + redirectsCount);
    }
}