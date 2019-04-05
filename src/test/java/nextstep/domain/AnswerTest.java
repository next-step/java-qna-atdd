package nextstep.domain;

import nextstep.exception.ObjectDeletedException;
import nextstep.exception.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

import static junit.framework.TestCase.assertTrue;

public class AnswerTest extends BaseTest {
    private static final User LOGIN_USER = new User("namjunemy", "1234", "njkim", "njkim@slipp.net");
    private static final User WRITER = new User("작성자", "test", "테스터", "테스터@slipp.net");

    private Answer answer;
    private Answer newAnswer;


    @Before
    public void setUp() throws Exception {
        //given
        WRITER.setId(1L);
        this.answer = new Answer(WRITER, "답변 내용");
        this.newAnswer = new Answer(answer.getId(), answer.getWriter(), answer.getQuestion(), "답변 수정");
    }


    @Test
    public void 수정_성공() {
        // When
        Answer updatedAnswer = answer.update(WRITER, newAnswer);

        // Then
        softly.assertThat(updatedAnswer.getContents()).isEqualTo("답변 수정");
    }

    @Test(expected = UnAuthorizedException.class)
    public void 수정_실패_UnAuthorizedException() {

        // When
        Answer updatedAnswer = answer.update(LOGIN_USER, newAnswer);
    }

    @Test(expected = ObjectDeletedException.class)
    public void 수정_실패_ObjectDeletedException() {
        // When
        answer.delete(WRITER);
        Answer updatedAnswer = answer.update(WRITER, newAnswer);
    }

    @Test
    public void 삭제_성공() {
        // When
        answer.delete(WRITER);

        //then
        assertTrue(answer.isDeleted());
    }

    @Test(expected = UnAuthorizedException.class)
    public void 삭제_실패_UnAuthorizedException() {
        // Given
        Answer answer = new Answer(WRITER, "답변 내용");

        // When
        answer.delete(LOGIN_USER);
    }

    @Test(expected = ObjectDeletedException.class)
    public void 삭제_실패_ObjectDeletedException() {
        // When
        answer.delete(WRITER);

        answer.delete(WRITER);
    }
}
