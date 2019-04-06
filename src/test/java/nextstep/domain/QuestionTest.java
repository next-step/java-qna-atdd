package nextstep.domain;

import static nextstep.domain.User.GUEST_USER;
import static nextstep.domain.UserTest.SANJIGI;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    public static final User WRITER = new User(1L, "weatherman", "password", "wea man", "today@weather.com");
    public static final Question WEATHER_QUESTION =
            new Question("오늘의 날씨는?", "강수 확률 높습니다! 우산 챙기세요")
                    .writeBy(WRITER);

    @Test
    public void update_owner() {
        Question target = new Question(WEATHER_QUESTION.getTitle(), WEATHER_QUESTION.getContents());
        WEATHER_QUESTION.update(WRITER, target);
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
        WEATHER_QUESTION.delete(WRITER);
        softly.assertThat(WEATHER_QUESTION.isDeleted()).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_not_owner() throws CannotDeleteException {
        WEATHER_QUESTION.delete(SANJIGI);
    }
}