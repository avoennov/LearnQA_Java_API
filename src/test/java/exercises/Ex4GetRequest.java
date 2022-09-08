package exercises;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class Ex4GetRequest {

    @Test
    /*
    В проекте нужно создать тест, который отправляет GET-запрос по адресу: https://playground.learnqa.ru/api/get_text
    Затем вывести содержимое текста в ответе на запрос. Когда тест будет готов - давайте его закоммитим в наш репозиторий.
    Результатом должна быть ссылка на коммит.
    */

    public void testGetRequest(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.print();
    }
}