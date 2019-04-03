package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class AnswerTest extends BaseTest {

    @Test
    public void update() {
        User loginUser = new User("javajigi", "test", "자바지기", "javajigi@slipp.net");

        Answer original = new Answer(loginUser, "contents");
        Answer updated = new Answer(loginUser, "updated contents");

        original.update(loginUser, updated);

        softly.assertThat(original.getContents()).isEqualTo(updated.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_by_unauthorized_user() {
        User writer = new User(1L, "javajigi", "test", "자바지기", "javajigi@slipp.net");
        Answer original = new Answer(writer, "contents");

        User loginUser = new User(2L,"sanjigi", "test", "산지기", "javajigi@slipp.net2");
        Answer updated = new Answer(loginUser, "updated contents");

        original.update(loginUser, updated);
    }

    @Test
    public void delete() {
        User loginUser = new User(1L, "javajigi", "test", "자바지기", "javajigi@slipp.net");
        Answer answer = new Answer(loginUser, "contents");

        DeleteHistory deleteHistory = answer.tryDelete(loginUser);

        softly.assertThat(answer.isDeleted()).isTrue();
        softly.assertThat(deleteHistory).isNotNull();
        softly.assertThat(deleteHistory.equalsEntity(answer)).isTrue();
    }


    @Test(expected = UnAuthorizedException.class)
    public void delete_by_unauthorized_user() {
        User writer = new User(1L, "javajigi", "test", "자바지기", "javajigi@slipp.net");
        Answer answer = new Answer(writer, "contents");
        User loginUser = new User(2L,"sanjigi", "test", "산지기", "javajigi@slipp.net2");

        answer.tryDelete(loginUser);
    }
}