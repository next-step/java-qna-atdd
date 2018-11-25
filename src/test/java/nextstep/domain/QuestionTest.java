package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {

    @Test(expected = UnAuthorizedException.class)
    public void 수정_다른작성자() throws Exception {
        User originUser = UserTest.JAVAJIGI;
        User loginUser = UserTest.SANJIGI;
        Question origin = new Question("수정전 제목", "수정전 내용");
        origin.writeBy(originUser);
        Question target = new Question("수정후 제목", "수정후 내용");
        origin.update(loginUser, target);
    }

    @Test
    public void 수정_원글작성자() throws Exception {
        User originUser = UserTest.JAVAJIGI;
        User loginUser = originUser;
        Question origin = new Question("수정전 제목", "수정전 내용");
        origin.writeBy(originUser);
        Question target = new Question("수정후 제목", "수정후 내용");
        origin.update(loginUser, target);
        softly.assertThat(origin.getTitle()).isEqualTo(target.getTitle());
        softly.assertThat(origin.getContents()).isEqualTo(target.getContents());
    }
}
