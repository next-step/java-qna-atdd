package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    public final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");
    public Question question;

    public static Question newQuestion(String title, String contents, User user) {
        return new Question(0L ,title, contents, user);
    }

    public static Question newQuestion(User user) {
        return newQuestion("title", "contents", user);
    }

    @Before
    public void init(){
        question = new Question("TDD를 배우는 이유는?", "리팩토링 향상을 위해");
        question.writeBy(JAVAJIGI);
    }

    @Test
    public void update_question(){
        Question target = newQuestion("TDD를 배우는 이유는?", "리팩토링 향상을 위해", JAVAJIGI);
        question.update(target);

        softly.assertThat(question.getTitle()).isEqualTo(target.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(target.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_question(){
        Question target = newQuestion(SANJIGI);
        question.update(target);
    }

    @Test
    public void delete_question() throws CannotDeleteException {
        question.writeBy(JAVAJIGI);
        question.delete(JAVAJIGI);

        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test
    public void delete_question_answer() throws CannotDeleteException {
        question.writeBy(JAVAJIGI);
        question.addAnswer(new Answer(JAVAJIGI, "TDD"));
        question.delete(JAVAJIGI);

        softly.assertThat(question.isDeleted()).isTrue();
        softly.assertThat(question.getAnswers().getAnswers().get(0).isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_cannot_question() throws CannotDeleteException {
        question.setDeleted(Boolean.TRUE);
        question.delete(JAVAJIGI);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_not_question() throws CannotDeleteException {
        question.writeBy(JAVAJIGI);
        question.delete(SANJIGI);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_not_question_not_user_answer() throws CannotDeleteException {
        question.writeBy(JAVAJIGI);
        question.addAnswer(new Answer(SANJIGI, "TDD"));
        question.delete(JAVAJIGI);
    }


}
