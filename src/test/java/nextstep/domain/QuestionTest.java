package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import nextstep.dto.QuestionDto;
import org.junit.*;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {

    private Question selfQuestion;
    private Question anotherQuestion;

    public static Question newQuestion() {
        return new Question("title", "contents");
    }

    public static Question newQuestion(String title, String contents) {
        return new Question(title, contents);
    }

    @Before
    public void setup() {
        selfQuestion = newQuestion("selfTitle", "selfContent");
        anotherQuestion = newQuestion("anotherTitle", "anotherContent");

        selfQuestion.writeBy(selfUser());
        anotherQuestion.writeBy(anotherUser());
    }

    @Test(expected = CannotUpdateException.class)
    public void update_another() throws Exception {
        selfQuestion.update(anotherUser(), new QuestionDto());
    }

    @Test
    public void update_self() throws Exception {
        QuestionDto updateQuestionDto = new QuestionDto("updateTitle", "updateContents");

        selfQuestion.update(selfUser(), updateQuestionDto);

        softly.assertThat(selfQuestion.getTitle()).isEqualTo(updateQuestionDto.getTitle());
        softly.assertThat(selfQuestion.getContents()).isEqualTo(updateQuestionDto.getContents());
        softly.assertThat(selfQuestion.getWriter()).isEqualTo(selfUser());
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_another() throws Exception {
        selfQuestion.delete(anotherUser());
    }

    @Test
    public void delete_self() throws Exception {
        selfQuestion.delete(selfUser());
        softly.assertThat(selfQuestion.isDeleted()).isTrue();
    }

    @Test
    public void add_answer() {
        Answer answer = new Answer(selfUser(), "answer");
        selfQuestion.addAnswer(answer);

        softly.assertThat(selfQuestion.getAnswers())
                .contains(answer);
    }
}
