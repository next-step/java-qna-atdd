package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import nextstep.dto.QuestionDto;
import org.junit.*;
import support.test.BaseTest;

import static nextstep.domain.AnswerTest.ANOTHER_ANSWER;
import static nextstep.domain.AnswerTest.SELF_ANSWER;
import static nextstep.domain.UserTest.ANOTHER_USER;
import static nextstep.domain.UserTest.SELF_USER;

public class QuestionTest extends BaseTest {

    public static final long SELF_QUESTION_ID = 1;
    public static final long ANOTHER_QUESTION_ID = 2;

    public static Question selfQuestion() {
        Question question = new Question("selfTitle", "selfContent");
        question.setId(SELF_QUESTION_ID);
        question.writeBy(SELF_USER);
        return question;
    }

    public static Question anotherQuestion() {
        Question question = new Question("selfTitle", "selfContent");
        question.setId(ANOTHER_QUESTION_ID);
        question.writeBy(ANOTHER_USER);
        return question;
    }

    public static Question newQuestion() {
        return new Question("title", "contents");
    }

    @Test(expected = CannotUpdateException.class)
    public void update_another() throws Exception {
        selfQuestion().update(ANOTHER_USER, new QuestionDto());
    }

    @Test
    public void update_self() throws Exception {
        Question question = selfQuestion();

        QuestionDto updateQuestionDto = new QuestionDto("updateTitle", "updateContents");
        question.update(SELF_USER, updateQuestionDto);

        softly.assertThat(question.getTitle()).isEqualTo(updateQuestionDto.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(updateQuestionDto.getContents());
        softly.assertThat(question.getWriter()).isEqualTo(SELF_USER);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_another() throws Exception {
        selfQuestion().delete(ANOTHER_USER);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_self_contains_another_answers() throws Exception {
        Question question = selfQuestion();
        question.addAnswer(ANOTHER_ANSWER);

        question.delete(SELF_USER);
    }

    @Test
    public void delete_self_only_self_answers() throws Exception {
        Question question = selfQuestion();
        question.addAnswer(SELF_ANSWER);

        question.delete(SELF_USER);

        softly.assertThat(question.isDeleted()).isTrue();
        softly.assertThat(question.getAnswers())
                .allMatch(answer -> answer.isDeleted());
    }

    @Test
    public void delete_self_empty_answers() throws Exception {
        Question question = new Question("title", "contents");
        question.writeBy(SELF_USER);

        question.delete(SELF_USER);

        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test
    public void add_answer() {
        Question question = selfQuestion();

        Answer answer = new Answer(SELF_USER, "answer");
        question.addAnswer(answer);

        softly.assertThat(question.getAnswers())
                .contains(answer);
    }
}
