package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.SANJIGI;

public class AnswerTest extends BaseTest {

    Answer answer_java = new Answer(1L, JAVAJIGI, null, "test1");
    Answer answer_san = new Answer(1L, SANJIGI, null, "test1");

    @Test
    public void 삭제_작성자_답변자_같음() {
        DeleteHistory deleteHistory= answer_java.delete(JAVAJIGI);
        System.out.println(deleteHistory);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 삭제_작성자_답변자_다름() {
        DeleteHistory deleteHistory= answer_java.delete(SANJIGI);
        System.out.println(deleteHistory);
    }
}
