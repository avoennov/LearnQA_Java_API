package exercises;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;


public class Ex5ParseJSON {

    @Test
    /*
    В рамках этой задачи нужно создать тест, который будет делать GET-запрос на адрес
    https://playground.learnqa.ru/api/get_json_homework
    Полученный JSON необходимо распечатать и изучить. Мы увидим, что это данные с сообщениями и временем,
    когда они были написаны. Наша задача вывести текст второго сообщения.
    Ответом должна быть ссылка на тест в вашем репозитории.
    */

    public void testParseJSON(){

        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        response.prettyPrint();

        String message = response.get("messages.message[1]");
        System.out.println("Second message value is:\n" + message);
    }
}