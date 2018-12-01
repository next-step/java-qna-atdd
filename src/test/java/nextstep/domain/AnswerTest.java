package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class AnswerTest extends BaseTest {

    @Test
    public void 내_답변_수정() {
        Answer answer = new Answer(UserTest.JAVAJIGI, "답변합니다");
        answer.update(UserTest.JAVAJIGI, "수정합니다");

        softly.assertThat(answer.getContents()).isEqualTo("수정합니다");
    }

    @Test(expected = UnAuthorizedException.class)
    public void 내_답변이_아니면_수정_불가() {
        Answer answer = new Answer(UserTest.JAVAJIGI, "답변합니다");
        answer.update(UserTest.SANJIGI, "수정합니다");
    }

    @Test
    public void 내_답변_삭제() {
        Answer answer = new Answer(UserTest.JAVAJIGI, "답변합니다");
        answer.delete(UserTest.JAVAJIGI);

        softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 내_답변이_아니면_삭제_불가() {
        Answer answer = new Answer(UserTest.JAVAJIGI, "답변합니다");
        answer.delete(UserTest.SANJIGI);
    }
}