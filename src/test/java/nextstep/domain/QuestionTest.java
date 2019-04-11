package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import nextstep.dto.QuestionDto;
import org.junit.*;
import support.test.BaseTest;

import static nextstep.domain.AnswerTest.ANOTHER_ANSWER_OF_DEFAULT_QUESTION;
import static nextstep.domain.AnswerTest.SELF_ANSWER_OF_DEFAULT_QUESTION;
import static nextstep.domain.UserTest.ANOTHER_USER;
import static nextstep.domain.UserTest.SELF_USER;

public class QuestionTest extends BaseTest {

    public static final Question SELF_QUESTION = new Question("selfTitle", "selfContent");
    public static final Question ANOTHER_QUESTION = new Question("anotherTitle", "anotherContent");

    public static final long SELF_QUESTION_ID = 1;
    public static final long ANOTHER_QUESTION_ID = 2;

    static {
        SELF_QUESTION.setId(SELF_QUESTION_ID);
        SELF_QUESTION.writeBy(SELF_USER);
        SELF_QUESTION.addAnswer(SELF_ANSWER_OF_DEFAULT_QUESTION);
        SELF_QUESTION.addAnswer(ANOTHER_ANSWER_OF_DEFAULT_QUESTION);

        ANOTHER_QUESTION.setId(ANOTHER_QUESTION_ID);
        ANOTHER_QUESTION.writeBy(ANOTHER_USER);
    }

    public static Question newQuestion() {
        return new Question("title", "contents");
    }

    @Test(expected = CannotUpdateException.class)
    public void update_another() throws Exception {
        SELF_QUESTION.update(ANOTHER_USER, new QuestionDto());
    }

    @Test
    public void update_self() throws Exception {
        QuestionDto updateQuestionDto = new QuestionDto("updateTitle", "updateContents");

        SELF_QUESTION.update(SELF_USER, updateQuestionDto);

        softly.assertThat(SELF_QUESTION.getTitle()).isEqualTo(updateQuestionDto.getTitle());
        softly.assertThat(SELF_QUESTION.getContents()).isEqualTo(updateQuestionDto.getContents());
        softly.assertThat(SELF_QUESTION.getWriter()).isEqualTo(SELF_USER);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_another() throws Exception {
        SELF_QUESTION.delete(ANOTHER_USER);
    }

    @Test
    public void delete_self() throws Exception {
        SELF_QUESTION.delete(SELF_USER);
        softly.assertThat(SELF_QUESTION.isDeleted()).isTrue();
    }

    @Test
    public void add_answer() {
        Answer answer = new Answer(SELF_USER, "answer");
        SELF_QUESTION.addAnswer(answer);

        softly.assertThat(SELF_QUESTION.getAnswers())
                .contains(answer);
    }
}
