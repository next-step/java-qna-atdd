package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static nextstep.domain.QuestionTest.SANGGU;
import static nextstep.domain.QuestionTest.newQuestion;
import static nextstep.domain.UserTest.JAVAJIGI;

public class AnswerTest extends BaseTest {

    static final Answer newAnswer(long id, String contents) {
        return new Answer(id, JAVAJIGI, newQuestion("title", "contensts"), contents);
    }

    static final Answer newAnswer(String contents) {
        return new Answer(0L, JAVAJIGI, newQuestion("title", "contensts"), contents);
    }

    static final Answer newAnswerAnoterUser(String contents) {
        return new Answer(0L, SANGGU, newQuestion("title", "contensts"), contents);
    }

    @Test
    public void delete_test() {
        Answer answer = newAnswer("댓글 내용");

        answer.deleteAnswer(JAVAJIGI);

        softly.assertThat(answer.isDeleted()).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_no_login_test() {
        Answer answer = newAnswer("댓글 내용");

        answer.deleteAnswer(SANGGU);
    }
}
