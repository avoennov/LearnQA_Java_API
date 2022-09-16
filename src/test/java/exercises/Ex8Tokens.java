package exercises;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class Ex8Tokens {

    @Test
    /*
    Иногда API-метод выполняет такую долгую задачу, что за один HTTP-запрос от него нельзя сразу получить готовый ответ.
    Это может быть подсчет каких-то сложных вычислений или необходимость собрать информацию по разным источникам.
    В этом случае на первый запрос API начинает выполнения задачи, а на последующие ЛИБО говорит, что задача еще не готова,
    ЛИБО выдает результат. Сегодня я предлагаю протестировать такой метод.
    Сам API-метод находится по следующему URL: https://playground.learnqa.ru/ajax/api/longtime_job
    Если мы вызываем его БЕЗ GET-параметра token, метод заводит новую задачу, а в ответ выдает нам JSON со следующими полями:
    * seconds - количество секунд, через сколько задача будет выполнена
    * token - тот самый токен, по которому можно получить результат выполнения нашей задачи
    Если же вызвать API-метод, УКАЗАВ GET-параметром token, то мы получим следующий JSON:
    * error - будет только в случае, если передать token, для которого не создавалась задача. В этом случае в ответе будет следующая надпись - No job linked to this token
    * status - если задача еще не готова, будет надпись Job is NOT ready, если же готова - будет надпись Job is ready
    * result - будет только в случае, если задача готова, это поле будет содержать результат
    Наша задача - написать тест, который сделал бы следующее:
    1) создавал задачу
    2) делал один запрос с token ДО того, как задача готова, убеждался в правильности поля status
    3) ждал нужное количество секунд с помощью функции time.sleep() - для этого надо сделать import time
    4) делал бы один запрос c token ПОСЛЕ того, как задача готова, убеждался в правильности поля status и наличии поля result
    Как всегда, код нашей программы выкладываем ссылкой на коммит.
    */

    public void testTokens() throws InterruptedException {

        String url = "https://playground.learnqa.ru/ajax/api/longtime_job";

        JsonPath createNewTask = RestAssured
                .get(url)
                .jsonPath();
        createNewTask.prettyPrint();

        String token = createNewTask.get("token");
        int seconds = createNewTask.get("seconds");
        long waitJobIsReady = (seconds + 1)* 1000L;

        Map<String, String> params = new HashMap<>();
        params.put("token", token);

        for(int i = 0; i < 2; i++) {
            JsonPath response = RestAssured
                    .given()
                    .queryParams(params)
                    .get(url)
                    .jsonPath();

            response.prettyPrint();

            if(i < 1){
                assertEquals("Job is NOT ready", response.get("status"));
                System.out.println("Wait when job is ready...");
                Thread.sleep(waitJobIsReady);
            } else{
                assertEquals("Job is ready", response.get("status"));
                String result = response.get("result");
                System.out.println("Result is: " + result);
            }
        }
    }
}