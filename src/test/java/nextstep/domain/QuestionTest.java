package nextstep.domain;

import org.junit.*;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {

    Question question;
    User writer;


    @Before
    public void setup() {
         question = new Question("testTitle", "testContent");
         writer = new User(1, "testWriter", "pass", "작성자", "email@email.com");
         question.writeBy(writer);
    }

    @Test
    public void 수정() {
        Question updatedQuestion = new Question("testTitle", "testContent");
        question.update(updatedQuestion);

        softly.assertThat(question.getTitle()).isEqualTo(updatedQuestion.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(updatedQuestion.getContents());
        softly.assertThat(question.getWriter()).isEqualTo(writer);
    }

    @Test
    public void 삭제() {
        question.delete();
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test
    public void 작성자_검사() {
        softly.assertThat(question.isOwner(writer)).isTrue();
    }
}
