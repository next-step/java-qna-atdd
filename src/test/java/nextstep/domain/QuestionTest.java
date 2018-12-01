package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {

    private Question defaultQuestion;
    private User questionWriter;

    public static Question newQuestion() {
        return new Question("질문 제목", "질문 내용");
    }

    @Before
    public void setUp() throws Exception {
        defaultQuestion = newQuestion();
        questionWriter = UserTest.JAVAJIGI;
        defaultQuestion.writeBy(questionWriter);
    }

    @Test
    public void 질문_수정이_잘_된다() {
        Question target = new Question("수정 제목", "수정 내용");
        defaultQuestion.update(questionWriter, target);

        softly.assertThat(defaultQuestion.isEqualsTitleAndContents(target)).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 내_질문이_아니면_수정할_수_없다() {
        Question target = new Question("수정 제목", "수정 내용");
        defaultQuestion.update(UserTest.SANJIGI, target);
    }

    @Test
    public void 질문_삭제처리가_잘_된다() {
        defaultQuestion.delete(questionWriter);

        softly.assertThat(defaultQuestion.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 내_질문이_아니면_삭제할_수_없다() {
        defaultQuestion.delete(UserTest.SANJIGI);
    }
}
