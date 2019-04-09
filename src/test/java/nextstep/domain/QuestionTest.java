package nextstep.domain;

import nextstep.exception.CannotDeleteException;
import nextstep.exception.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    private static final User LOGIN_USER = new User("namjunemy", "1234", "njkim", "njkim@slipp.net");

    @Test
    public void 질문_수정_성공() {
        // Given
        Question question = new Question("제목", "내용");
        question.writeBy(LOGIN_USER);

        String updateTitle = "수정된 제목";
        String updateContents = "수정된 내용";
        Question target = new Question(updateTitle, updateContents);

        // When
        question.update(LOGIN_USER, target);

        // Then
        softly.assertThat(question.getTitle()).isEqualTo(updateTitle);
        softly.assertThat(question.getContents()).isEqualTo(updateContents);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문_수정_권한_없음() {
        // Given
        User writer = new User("테스터", "test", "테스터", "테스터@slipp.net");
        writer.setId(1L);

        Question question = new Question("제목", "내용");
        question.writeBy(writer);

        String updateTitle = "수정된 제목";
        String updateContents = "수정된 내용";
        Question target = new Question(updateTitle, updateContents);

        // When
        question.update(LOGIN_USER, target);
    }

    @Test
    public void 질문_삭제_성공_답변_없을때() {
        // Given
        Question question = new Question("제목", "내용");
        question.writeBy(LOGIN_USER);

        // When
        question.delete(LOGIN_USER);

        // Then
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test
    public void 질문_삭제_성공_전부_본인답변() {
        // Given
        User writer = new User(1L,"테스터", "test", "테스터", "테스터@slipp.net");

        Question question = new Question("제목", "내용");
        question.writeBy(writer);

        Answer answer1 = new Answer(writer, "답변 내용");
        Answer answer2 = new Answer(writer, "답변 내용");

        question.addAnswer(answer1);
        question.addAnswer(answer2);

        // When
        question.delete(writer);

        // Then
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void 질문_삭제_실패_전부_본인답변_아님() {
        // Given
        User writer = new User(1L,"테스터", "test", "테스터", "테스터@slipp.net");

        Question question = new Question("제목", "내용");
        question.writeBy(writer);

        Answer answer1 = new Answer(writer, "답변 내용");
        Answer answer2 = new Answer(LOGIN_USER, "답변 내용");

        question.addAnswer(answer1);
        question.addAnswer(answer2);

        // When
        question.delete(writer);

        // Then
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문_삭제_권한_없음() {
        // Given
        User writer = new User("테스터", "test", "테스터", "테스터@slipp.net");
        writer.setId(1L);
        Question question = new Question("제목", "내용");
        question.writeBy(writer);

        // When
        question.delete(LOGIN_USER);
    }
}
