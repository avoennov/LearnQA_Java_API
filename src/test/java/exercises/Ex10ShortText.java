package exercises;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Ex10ShortText {

    @Test
    /*
    В рамках этой задачи с помощью JUnit необходимо написать тест, который проверяет длину какой-то переменной типа
    String с помощью любого выбранного Вами метода assert.
    Если текст длиннее 15 символов, то тест должен проходить успешно. Иначе падать с ошибкой.
    Результатом должна стать ссылка на такой тест.
    */

    public void testShortText() {
        String text = "More than fifteen characters";
        //String text = "Short text";
        assertTrue(text.length() > 15, "Text shorter than 15 characters [Fail]");
    }

}
