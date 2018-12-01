package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

public class AnswerTest extends BaseTest {

    private Answer defaultAnswer;
    private User answerWriter;

    public static Answer newAnswer() {
        return new Answer(UserTest.JAVAJIGI, "답변합니다");
    }

    @Before
    public void setUp() throws Exception {
        answerWriter = UserTest.JAVAJIGI;
        defaultAnswer = newAnswer();
    }

    @Test
    public void 내_답변_수정() {
        defaultAnswer.update(answerWriter, "수정합니다");

        softly.assertThat(defaultAnswer.getContents()).isEqualTo("수정합니다");
    }

    @Test(expected = UnAuthorizedException.class)
    public void 내_답변이_아니면_수정_불가() {
        defaultAnswer.update(UserTest.SANJIGI, "수정합니다");
    }

    @Test
    public void 내_답변_삭제() {
        defaultAnswer.delete(answerWriter);

        softly.assertThat(defaultAnswer.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 내_답변이_아니면_삭제_불가() {
        defaultAnswer.delete(UserTest.SANJIGI);
    }
}