package nextstep.domain;

import nextstep.exception.ObjectDeletedException;
import nextstep.exception.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

import static junit.framework.TestCase.assertTrue;

public class AnswerTest extends BaseTest {
    private final User loginUser = new User("namjunemy", "1234", "njkim", "njkim@slipp.net");
    private final User writer = new User("작성자", "test", "테스터", "테스터@slipp.net");
    private Answer answer;

    @Before
    public void setUp() throws Exception {
        //given
        this.answer = new Answer(writer, "답변 내용");
    }


    @Test
    public void 수정_성공() {
        // Given

        // When
        Answer newAnswer = new Answer(answer.getId(), answer.getWriter(), answer.getQuestion(), "답변 수정");
        Answer updatedAnswer = answer.update(writer, newAnswer);

        // Then
        softly.assertThat(updatedAnswer.getContents()).isEqualTo("답변 수정");
    }

    @Test(expected = UnAuthorizedException.class)
    public void 수정_실패_UnAuthorizedException() {
        // Given

        // When
        writer.setId(2L);
        Answer newAnswer = new Answer(answer.getId(), answer.getWriter(), answer.getQuestion(), "답변 수정");
        Answer updatedAnswer = answer.update(loginUser, newAnswer);
    }

    @Test(expected = ObjectDeletedException.class)
    public void 수정_실패_ObjectDeletedException() {
        // Given

        // When
        answer.delete(writer);

        writer.setId(2L);
        Answer newAnswer = new Answer(answer.getId(), answer.getWriter(), answer.getQuestion(), "답변 수정");
        Answer updatedAnswer = answer.update(loginUser, newAnswer);
    }

    @Test
    public void 삭제_성공() {
        // Given

        // When
        answer.delete(writer);

        // Then
        assertTrue(answer.isDeleted());
    }

    @Test(expected = UnAuthorizedException.class)
    public void 삭제_실패_UnAuthorizedException() {
        // Given
        writer.setId(2L);
        Answer answer = new Answer(writer, "답변 내용");

        // When
        answer.delete(loginUser);
    }

    @Test(expected = ObjectDeletedException.class)
    public void 삭제_실패_ObjectDeletedException() {
        // Given
        answer.delete(writer);

        // When
        answer.delete(writer);
    }
}
