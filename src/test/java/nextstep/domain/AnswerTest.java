package nextstep.domain;

import nextstep.CannotDeleteException;
import org.junit.Test;
import support.test.BaseTest;

public class AnswerTest extends BaseTest {
    public static final Answer ORIGIN_ANSWER = new Answer(1L, UserTest.JAVAJIGI, QuestionTest.ORIGINAL_QUESTION, "answer");

    @Test
    public void 답변_생성() {
        Answer answer = new Answer(1L, UserTest.JAVAJIGI, QuestionTest.ORIGINAL_QUESTION, "answer");
        softly.assertThat(answer.getWriter()).isEqualTo(UserTest.JAVAJIGI);
        softly.assertThat(answer.getContents()).isEqualTo("answer");
    }

    @Test
    public void delete_answer() throws CannotDeleteException {
        ORIGIN_ANSWER.delete(UserTest.JAVAJIGI);
        softly.assertThat(ORIGIN_ANSWER.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_answer_not_owner() throws CannotDeleteException {
        ORIGIN_ANSWER.delete(UserTest.SANJIGI);
        softly.assertThat(ORIGIN_ANSWER.isDeleted()).isFalse();
    }
}
