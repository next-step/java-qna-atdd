package nextstep.domain;

import static nextstep.domain.User.GUEST_USER;
import static nextstep.domain.UserTest.SANJIGI;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    public static User WEATHER_QUESTION_WRITER = new User(1L, "weatherman", "password", "wea man", "today@weather.com");
    public static Question WEATHER_QUESTION = new Question("오늘의 날씨는?", "강수 확률 높습니다! 우산 챙기세요");

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
    public void delete_owner() throws CannotDeleteException {
        WEATHER_QUESTION.delete(WEATHER_QUESTION_WRITER);
        softly.assertThat(WEATHER_QUESTION.isDeleted()).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_not_owner() throws CannotDeleteException {
        WEATHER_QUESTION.delete(SANJIGI);
    }
}