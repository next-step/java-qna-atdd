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

    public static final Answer SELF_ANSWER = new Answer(SELF_USER, "selfAnswer");
    public static final Answer ANOTHER_ANSWER = new Answer(ANOTHER_USER, "anotherAnswer");

    public static final long SELF_ANSWER_ID = 1;
    public static final long ANOTHER_ANSWER_ID = 2;


    static {
        SELF_ANSWER.setId(SELF_ANSWER_ID);
        ANOTHER_ANSWER.setId(ANOTHER_ANSWER_ID);
    }

    public static Answer selfAnswer() {
        Answer answer = new Answer(SELF_USER, "selfAnswer");
        answer.setId(SELF_ANSWER_ID);
        return answer;
    }

    public static Answer anotherAnswer() {
        Answer answer = new Answer(ANOTHER_USER, "anotherAnswer");
        answer.setId(ANOTHER_ANSWER_ID);
        return answer;
    }

    @Test(expected = CannotUpdateException.class)
    public void update_another() throws Exception {
        SELF_ANSWER.update(ANOTHER_USER, "contents");
    }

    @Test
    public void update_self() throws Exception {
        String contents = "update contents";
        SELF_ANSWER.update(SELF_USER, contents);

        softly.assertThat(SELF_ANSWER.getContents()).isEqualTo(contents);
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

        answer.delete(SELF_USER);
        softly.assertThat(answer.isDeleted()).isTrue();
    }
}