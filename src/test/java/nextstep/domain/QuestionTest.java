package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    User originUser;
    Question origin;

    @Before
    public void setUp() throws Exception {
        originUser = UserTest.JAVAJIGI;
        origin = new Question("질문있습니다.", "내용입니다.");
        origin.writeBy(originUser);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 수정_다른작성자() throws Exception {
        User loginUser = UserTest.SANJIGI;
        Question target = new Question("수정후 제목", "수정후 내용");
        origin.update(loginUser, target);
    }

    @Test
    public void 수정_원글작성자() throws Exception {
        User loginUser = originUser;
        Question target = new Question("수정후 제목", "수정후 내용");
        origin.update(loginUser, target);
        softly.assertThat(origin.getTitle()).isEqualTo(target.getTitle());
        softly.assertThat(origin.getContents()).isEqualTo(target.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void 삭제_다른작성자() throws Exception {
        User loginUser = UserTest.SANJIGI;
        origin.delete(loginUser);
    }

    @Test(expected = CannotDeleteException.class)
    public void 삭제_원글작성자_지워진_질문() throws Exception {
        User loginUser = originUser;
        origin.delete(loginUser);
        origin.delete(loginUser);
    }

    @Test
    public void 삭제_원글작성자() throws Exception {
        User loginUser = originUser;
        softly.assertThat(origin.delete(loginUser)).isTrue();
    }
}
