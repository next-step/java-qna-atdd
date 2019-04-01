package nextstep.domain;

import static nextstep.domain.UserTest.newUser;
import static org.assertj.core.api.Assertions.assertThat;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    public static final User WEATHERMAN = new User(1L, "weatherman", "password", "wea man", "today@weather.com");
    public static final Question QUESTION_WEATHER = new Question("오늘의 날씨는?", "강수 확률 높습니다! 우산 챙기세요");

    @Test(expected = UnAuthorizedException.class)
    public void update_guest() {
        Question origin = new Question(QUESTION_WEATHER.getTitle(), QUESTION_WEATHER.getContents());
        origin.writeBy(WEATHERMAN);

        Question target = new Question(QUESTION_WEATHER.getTitle() + "2 !!", QUESTION_WEATHER.getContents() + "2 ....");
        origin.update(User.GUEST_USER, target);
    }

    @Test
    public void update_owner() {
        Question origin = new Question(QUESTION_WEATHER.getTitle(), QUESTION_WEATHER.getContents());
        origin.writeBy(WEATHERMAN);

        Question target = new Question(QUESTION_WEATHER.getTitle() + "2 !!", QUESTION_WEATHER.getContents() + "2 ....");
        origin.update(WEATHERMAN, target);

        assertThat(origin.getTitle()).isEqualTo(target.getTitle());
        assertThat(origin.getContents()).isEqualTo(target.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        Question origin = new Question(QUESTION_WEATHER.getTitle(), QUESTION_WEATHER.getContents());
        origin.writeBy(WEATHERMAN);

        Question target = new Question(QUESTION_WEATHER.getTitle() + "2 !!", QUESTION_WEATHER.getContents() + "2 ....");
        origin.update(new User("other", "password", "other", "other@test.com"), target);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_guest() throws CannotDeleteException {
        Question origin = new Question(QUESTION_WEATHER.getTitle(), QUESTION_WEATHER.getContents());
        origin.writeBy(WEATHERMAN);

        origin.delete(User.GUEST_USER);
    }

    @Test
    public void delete_owner() throws CannotDeleteException {
        Question origin = new Question(QUESTION_WEATHER.getTitle(), QUESTION_WEATHER.getContents());
        origin.writeBy(WEATHERMAN);

        origin.delete(WEATHERMAN);

        assertThat(origin.isDeleted()).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_not_owner() throws CannotDeleteException {
        Question origin = new Question(QUESTION_WEATHER.getTitle(), QUESTION_WEATHER.getContents());
        origin.writeBy(WEATHERMAN);

        origin.delete(newUser(2L, "other", "passwd"));
    }
}