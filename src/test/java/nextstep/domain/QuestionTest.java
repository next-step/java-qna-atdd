package nextstep.domain;

import static nextstep.domain.User.GUEST_USER;
import static nextstep.domain.UserTest.SANJIGI;
import static nextstep.domain.UserTest.newUser;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    private User WEATHER_QUESTION_WRITER = new User(1L, "weatherman", "password", "wea man", "today@weather.com");
    private Question WEATHER_QUESTION = new Question("오늘의 날씨는?", "강수 확률 높습니다! 우산 챙기세요");

    @Before
    public void setUp() {
        WEATHER_QUESTION = WEATHER_QUESTION.writeBy(WEATHER_QUESTION_WRITER);
    }

    @Test
    public void update_owner() {
        Question target = new Question(WEATHER_QUESTION.getTitle(), WEATHER_QUESTION.getContents());
        WEATHER_QUESTION.update(WEATHER_QUESTION_WRITER, target);
        softly.assertThat(WEATHER_QUESTION.equalsTitleAndContents(target)).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_guest() {
        Question target = new Question(WEATHER_QUESTION.getTitle(), WEATHER_QUESTION.getContents());
        WEATHER_QUESTION.update(GUEST_USER, target);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        Question target = new Question(WEATHER_QUESTION.getTitle() + "2 !!", WEATHER_QUESTION.getContents() + "2 ....");
        WEATHER_QUESTION.update(SANJIGI, target);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_guest() throws CannotDeleteException {
        WEATHER_QUESTION.delete(GUEST_USER);
    }

    @Test
    public void delete_작성자_맞음() throws CannotDeleteException {
        WEATHER_QUESTION.delete(WEATHER_QUESTION_WRITER);
        softly.assertThat(WEATHER_QUESTION.isDeleted()).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_작성자_아님() throws CannotDeleteException {
        WEATHER_QUESTION.delete(SANJIGI);
    }

    @Test
    public void delete_작성자_답변만있음() throws CannotDeleteException {
        Answer answer = new Answer(WEATHER_QUESTION_WRITER, "답변 입니다~");
        WEATHER_QUESTION.addAnswer(answer);
        WEATHER_QUESTION.delete(WEATHER_QUESTION_WRITER);
        softly.assertThat(WEATHER_QUESTION.isDeleted()).isEqualTo(true);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_다른사람_답변있음() throws CannotDeleteException {
        Answer answer = new Answer(newUser(2L), "답변 입니다~");
        WEATHER_QUESTION.addAnswer(answer);
        WEATHER_QUESTION.delete(WEATHER_QUESTION_WRITER);
    }
}