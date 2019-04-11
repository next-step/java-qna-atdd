package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import org.junit.*;
import support.test.BaseTest;

import static nextstep.domain.QuestionTest.anotherQuestion;
import static nextstep.domain.QuestionTest.selfQuestion;
import static nextstep.domain.UserTest.ANOTHER_USER;
import static nextstep.domain.UserTest.SELF_USER;

public class AnswerTest extends BaseTest {

    public static final long SELF_ANSWER_ID = 1;
    public static final long ANOTHER_ANSWER_ID = 2;

    public static Answer selfAnswer() {
        return newAnswer(SELF_ANSWER_ID, SELF_USER, "selfAnswer");
    }

    public static Answer anotherAnswer() {
        return newAnswer(ANOTHER_ANSWER_ID, ANOTHER_USER, "anotherAnswer");
    }

    private static Answer newAnswer(long id, User writer, String contents) {
        Answer answer = new Answer(writer, contents);
        answer.setId(id);
        return answer;
    }

    @Test(expected = CannotUpdateException.class)
    public void update_another() throws Exception {
        Answer answer = selfAnswer();

        answer.update(ANOTHER_USER, "contents");
    }

    @Test
    public void update_self() throws Exception {
        String contents = "update contents";
        Answer answer = selfAnswer();

        answer.update(SELF_USER, contents);

        softly.assertThat(answer.getContents()).isEqualTo(contents);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_another() throws Exception {
        Answer answer = selfAnswer();
        answer.delete(ANOTHER_USER);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_self_different_owner_from_question() throws Exception {
        Question question = anotherQuestion();
        Answer answer = selfAnswer();
        question.addAnswer(answer);

        answer.delete(SELF_USER);
    }

    @Test
    public void delete_self_same_owner_from_question() throws Exception {
        Question question = selfQuestion();
        Answer answer = selfAnswer();
        question.addAnswer(answer);

        DeleteHistory deleteHistory = answer.delete(SELF_USER);
        softly.assertThat(answer.isDeleted()).isTrue();
        softly.assertThat(deleteHistory).isNotNull();
    }
}