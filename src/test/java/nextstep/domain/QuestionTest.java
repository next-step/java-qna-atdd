package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {

    @Test
    public void createQuestionTest() {
        // given
        String title = "타이틀";
        String contents = "컨텐츠";

        // when
        Question question = new Question(title, contents, basicUser);

        // then
        softly.assertThat(question.getTitle()).isEqualTo(title);
        softly.assertThat(question.getContents()).isEqualTo(contents);
        softly.assertThat(question.getWriter()).isEqualTo(basicUser);
    }

    @Test
    public void updateQuestion() {
        // given
        Question question = new Question("타이틀", "컨텐츠", basicUser);
        String newTitle = "수정된 타이틀";
        String newContents = "수정된 컨텐츠";

        // when
        question.update(basicUser, new Question(newTitle, newContents));

        //then
        softly.assertThat(question.getTitle()).isEqualTo(newTitle);
        softly.assertThat(question.getContents()).isEqualTo(newContents);
        softly.assertThat(question.getWriter()).isEqualTo(basicUser);
    }


    @Test(expected = UnAuthorizedException.class)
    public void updateQuestionWithInvalidUser() {
        // given
        Question question = new Question("타이틀", "컨텐츠", basicUser);

        // when
        question.update(anotherUser, new Question(" ", " "));
    }

    @Test
    public void deleteQuest() {
        // given
        Question question = new Question("타이틀", "컨텐츠", basicUser);

        // when
        question.delete(basicUser);

        //then
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteQuestWithInvalidUser() {
        // given
        Question question = new Question("타이틀", "컨텐츠", basicUser);

        // when
        question.delete(anotherUser);
    }


}
