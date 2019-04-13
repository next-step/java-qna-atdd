package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import nextstep.dto.QuestionDto;
import org.junit.*;
import support.test.BaseTest;

import java.util.List;

import static nextstep.domain.AnswerTest.anotherAnswer;
import static nextstep.domain.AnswerTest.selfAnswer;
import static nextstep.domain.UserTest.ANOTHER_USER;
import static nextstep.domain.UserTest.SELF_USER;

public class QuestionTest extends BaseTest {

    public static final long SELF_QUESTION_ID = 1;
    public static final long ANOTHER_QUESTION_ID = 2;

    public static Question selfQuestion() {
        return newQuestion(SELF_QUESTION_ID, SELF_USER, "selfTitle", "selfContents");
    }

    public static Question anotherQuestion() {
        return newQuestion(ANOTHER_QUESTION_ID, ANOTHER_USER, "anotherTitle", "anotherContents");
    }

    private static Question newQuestion(long id, User writer, String title, String content) {
        Question question = new Question(title, content);
        question.setId(id);
        question.writeBy(writer);
        return question;
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
        question.addAnswer(anotherAnswer());

        question.delete(SELF_USER);
    }

    @Test
    public void delete_self_contains_only_self_answers() throws Exception {
        Question question = selfQuestion();
        question.addAnswer(selfAnswer());

        List<DeleteHistory> deleteHistories = question.delete(SELF_USER);

        softly.assertThat(question.isDeleted()).isTrue();
        softly.assertThat(question.isDeletedWithAllAnswers()).isTrue();
        softly.assertThat(deleteHistories).hasSize(2);
    }

    @Test
    public void delete_self_empty_answers() throws Exception {
        Question question = new Question("title", "contents");
        question.writeBy(SELF_USER);

        List<DeleteHistory> deleteHistories = question.delete(SELF_USER);

        softly.assertThat(question.isDeleted()).isTrue();
        softly.assertThat(deleteHistories)
                .hasSize(1);
    }

    @Test
    public void add_answer() {
        Question question = selfQuestion();

        Answer answer = new Answer(SELF_USER, "answer");
        question.addAnswer(answer);

        softly.assertThat(question.sizeAnswers()).isEqualTo(1);
    }
}
