package nextstep.domain;

import nextstep.CannotDeleteException;
import org.junit.Test;
import support.test.BaseTest;

public class AnswerTest extends BaseTest {
    private Question question = new Question("title", "contents");
    private User answerWriter = UserTest.JAVAJIGI;
    private Answer answer = new Answer(1L, this.answerWriter, this.question, "answer");

    @Test
    public void 답변_생성() {
        Answer answer = new Answer(1L, UserTest.JAVAJIGI, this.question, "answer");
        softly.assertThat(answer.getWriter()).isEqualTo(UserTest.JAVAJIGI);
        softly.assertThat(answer.getContents()).isEqualTo("answer");
    }

    @Test
    public void delete_answer() throws CannotDeleteException {
        this.answer.delete(this.answerWriter);
        softly.assertThat(this.answer.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_answer_not_owner() throws CannotDeleteException {
        this.answer.delete(UserTest.SANJIGI);
        softly.assertThat(this.answer.isDeleted()).isFalse();
    }
}
