package nextstep.domain;

import nextstep.NotFoundException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class AnswerTest extends BaseTest {
    private User writer = UserTest.newUser(1L);
    private Question question = QuestionTest.newQuestion(1L);

    @Test
    public void 답변을_생성한다() {
        Answer answer = new Answer(writer, question, "This is answer");

        softly.assertThat(answer.getWriter()).isEqualTo(writer);
        softly.assertThat(answer.getQuestion()).isEqualTo(question);
        softly.assertThat(answer.getContents()).isEqualTo("This is answer");
    }

    @Test(expected = UnAuthorizedException.class)
    public void 작성자없이_답변을_달수없다() {
        new Answer(null, question, "This is title");
    }

    @Test(expected = NotFoundException.class)
    public void 없는_질문에_답변을_달수없다() {
        new Answer(writer, null, "This is contents");
    }
}
