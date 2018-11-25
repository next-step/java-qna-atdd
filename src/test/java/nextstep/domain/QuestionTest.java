package nextstep.domain;

import nextstep.CannotDeleteException;
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

    @Test(expected = UnAuthorizedException.class)
    public void 삭제_다른작성자() throws Exception {
        User originUser = UserTest.JAVAJIGI;
        User loginUser = UserTest.SANJIGI;
        Question origin = new Question("삭제할 질문의 제목", "삭제할 질문의 내용");
        origin.writeBy(originUser);
        origin.delete(loginUser);
    }

    @Test(expected = CannotDeleteException.class)
    public void 삭제_원글작성자_지워진_질문() throws Exception {
        User originUser = UserTest.JAVAJIGI;
        User loginUser = originUser;
        Question origin = new Question("삭제할 질문의 제목", "삭제할 질문의 내용");
        origin.writeBy(originUser);
        origin.delete(loginUser);
        origin.delete(loginUser);
    }

    @Test
    public void 삭제_원글작성자() throws Exception {
        User originUser = UserTest.JAVAJIGI;
        User loginUser = originUser;
        Question origin = new Question("삭제할 질문의 제목", "삭제할 질문의 내용");
        origin.writeBy(originUser);
        softly.assertThat(origin.delete(loginUser)).isTrue();
    }
}
