package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {

    @Test(expected = UnAuthorizedException.class)
    public void 질문_수정_권한없음() {
        User loginUser = new User("seoyeong", "test", "sy", "sy@slipp.net");
        loginUser.setId(100L);
        User otherUser = new User("sysy", "test", "sysy", "sysy@slipp.net");
        otherUser.setId(200L);
        Question question = new Question("제목입니다.", "내용입니다.");
        question.writeBy(loginUser);
        question.update(otherUser, new Question("수정될 제목", "수정 내용입니다."));
    }

    @Test
    public void 질문_수정_성공() {
        User loginUser = new User("seoyeong", "test", "sy", "sy@slipp.net");
        Question question = new Question("제목입니다.", "내용입니다.");
        question.writeBy(loginUser);
        question.update(loginUser, new Question("수정될 제목", "수정될 내용입니다"));
        softly.assertThat(question.getTitle()).isEqualTo("수정될 제목");
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문_삭제_권한없음() {
        User loginUser = new User("seoyeong", "test", "sy", "sy@slipp.net");
        loginUser.setId(100L);
        User otherUser = new User("sysy", "test", "sysy", "sysy@slipp.net");
        otherUser.setId(200L);
        Question question = new Question("제목입니다.", "내용입니다.");
        question.writeBy(loginUser);
        question.delete(otherUser);
    }

    @Test
    public void 질문_삭제_성공() {
        User loginUser = new User("seoyeong", "test", "sy", "sy@slipp.net");
        Question question = new Question("제목입니다.", "내용입니다.");
        question.writeBy(loginUser);
        question.delete(loginUser);
        softly.assertThat(question.isDeleted()).isTrue();
    }
}
