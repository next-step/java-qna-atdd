package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

public class AnswerTest extends BaseTest {
    private User originUser;
    private Answer origin;


    @Before
    public void setUp() throws Exception {
        originUser = UserTest.JAVAJIGI;
        origin = newAnswer();
        origin.setId(1000);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 수정_다른작성자() throws Exception {
        User loginUser = UserTest.SANJIGI;
        Answer target = new Answer(loginUser, "수정 시도");
        origin.update(loginUser, target);
    }

    @Test
    public void 수정_원글작성자() throws Exception {
        User loginUser = originUser;
        Answer target = new Answer(loginUser, "수정 시도");
        origin.update(loginUser, target);
        softly.assertThat(origin.getContents()).isEqualTo(target.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void 삭제_다른작성자() throws Exception {
        User loginUser = UserTest.SANJIGI;
        origin.delete(loginUser);
    }

    @Test(expected = CannotDeleteException.class)
    public void 삭제_원답변작성자_지워진_질문() throws Exception {
        User loginUser = originUser;
        origin.delete(loginUser);
        origin.delete(loginUser);
    }

    @Test
    public void 삭제_원글작성자() throws Exception {
        User loginUser = originUser;
        softly.assertThat(origin.delete(loginUser).isMatchContentId(origin.getId())).isTrue();
    }

    public static Answer newAnswer() {
        return new Answer(UserTest.JAVAJIGI, "새로운 답변");
    }
}
