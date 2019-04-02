package nextstep.domain;

import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    @Test
    public void 작성자가_맞는지_확인한다() {
        User user = new User("myId", "myPassword", "myName", "myEmail");
        Question question = new Question("This is title", "This is contents");
        question.writeBy(user);

        softly.assertThat(question.isOwner(user)).isTrue();
    }
}
