package nextstep.domain;

import nextstep.exception.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {

    @Test
    public void 질문_수정_성공() {
        // Given
        User loginUser = new User("namjunemy", "1234", "njkim", "njkim@slipp.net");
        Question question = new Question("제목", "내용");
        question.writeBy(loginUser);

        String updateTitle = "수정된 제목";
        String updateContents = "수정된 내용";
        Question target = new Question(updateTitle, updateContents);

        // When
        question.update(loginUser, target);

        // Then
        softly.assertThat(question.getTitle()).isEqualTo(updateTitle);
        softly.assertThat(question.getContents()).isEqualTo(updateContents);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문_수정_권한_없음() {
        // Given
        User loginUser = new User("namjunemy", "1234", "njkim", "njkim@slipp.net");

        User writer = new User("테스터", "test", "테스터", "테스터@slipp.net");
        writer.setId(1L);
        Question question = new Question("제목", "내용");
        question.writeBy(writer);

        String updateTitle = "수정된 제목";
        String updateContents = "수정된 내용";
        Question target = new Question(updateTitle, updateContents);

        // When
        question.update(loginUser, target);
    }

    @Test
    public void 질문_삭제_성공() {
        // Given
        User loginUser = new User("namjunemy", "1234", "njkim", "njkim@slipp.net");
        Question question = new Question("제목", "내용");
        question.writeBy(loginUser);

        // When
        question.delete(loginUser);

        // Then
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문_삭제_권한_없음() {
        // Given
        User loginUser = new User("namjunemy", "1234", "njkim", "njkim@slipp.net");

        User writer = new User("테스터", "test", "테스터", "테스터@slipp.net");
        writer.setId(1L);
        Question question = new Question("제목", "내용");
        question.writeBy(writer);

        // When
        question.delete(loginUser);
    }
}
