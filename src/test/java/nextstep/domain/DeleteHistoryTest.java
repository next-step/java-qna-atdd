package nextstep.domain;

import org.junit.Test;
import support.test.BaseTest;

public class DeleteHistoryTest extends BaseTest {

    @Test
    public void 생성_질문_삭제_기록() {
        User loginUser = UserTest.JAVAJIGI;
        Question question = new Question("제목", "내용");
        question.writeBy(loginUser);
        softly.assertThat(DeleteHistory.fromQuestion(UserTest.JAVAJIGI, question)).isNotNull();
    }

    @Test
    public void 생성_답변_삭제_기록() {
        User loginUser = UserTest.JAVAJIGI;
        Answer answer = new Answer(loginUser, "답변 내용");
        softly.assertThat(DeleteHistory.fromAnswer(UserTest.JAVAJIGI, answer)).isNotNull();
    }
}