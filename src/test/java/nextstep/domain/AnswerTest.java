package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
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
    public void 답변_삭제() throws CannotDeleteException {
        Answer answer = new Answer(1L, UserTest.JAVAJIGI, QuestionTest.ORIGINAL_QUESTION, "answer");
        answer.delete(UserTest.JAVAJIGI);
        softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 답변_작성자가_다른_경우() throws CannotDeleteException {
        Answer answer = new Answer(1L, UserTest.JAVAJIGI, QuestionTest.ORIGINAL_QUESTION, "answer");
        answer.delete(UserTest.SANJIGI);
    }
}
