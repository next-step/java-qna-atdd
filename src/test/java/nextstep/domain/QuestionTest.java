package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {

    @Test
    public void 질문_수정이_잘_된다() {
        User writer = new User(1L, "dicorndl", "password", "dicorndl", "dicorndl@gmail.com");

        Question existing = new Question("기존 제목", "기존 내용");
        existing.writeBy(writer);

        Question target = new Question("수정 제목", "수정 내용");
        existing.update(writer, target);

        softly.assertThat(existing.getTitle()).isEqualTo(target.getTitle());
        softly.assertThat(existing.getContents()).isEqualTo(target.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void 내_질문이_아니면_수정할_수_없다() {
        User writer = new User(1L, "dicorndl", "password", "dicorndl", "dicorndl@gmail.com");

        Question existing = new Question("기존 제목", "기존 내용");
        existing.writeBy(writer);

        Question target = new Question("수정 제목", "수정 내용");
        existing.update(new User(), target);
    }
}
