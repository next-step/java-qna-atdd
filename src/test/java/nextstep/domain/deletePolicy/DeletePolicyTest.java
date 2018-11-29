package nextstep.domain.deletePolicy;

import nextstep.domain.Answer;
import nextstep.domain.DeletePolicy;
import nextstep.domain.Question;
import org.junit.Test;
import support.test.BaseTest;

import static nextstep.domain.AnswerTest.newAnswer;
import static nextstep.domain.QuestionTest.newQuestion;
import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.SANJIGI;

public class DeletePolicyTest extends BaseTest {

    DeletePolicy deletePolicy = new DefaultDeleteQuestionPolicy();

    @Test
    public void 질문자_면서_답변_없는_경우() {
        Question question = newQuestion(SANJIGI);
        softly.assertThat(deletePolicy.canPermission(question, SANJIGI)).isTrue();
    }

    @Test
    public void 질문자_아니고_답변_없는_경우() {
        Question question = newQuestion(JAVAJIGI);
        softly.assertThat(deletePolicy.canPermission(question, SANJIGI)).isFalse();
    }

    @Test
    public void 질문자_면서_답변_같은_사람() {
        Answer answer = newAnswer(SANJIGI);
        Question question = newQuestion(SANJIGI);
        question.addAnswer(answer);

        softly.assertThat(deletePolicy.canPermission(question, SANJIGI)).isTrue();

    }

    @Test
    public void 질문자_면서_답변_다른_사람() {
        Answer answer = newAnswer();
        Question question = newQuestion(SANJIGI);
        question.addAnswer(answer);

        softly.assertThat(deletePolicy.canPermission(question, SANJIGI)).isFalse();

    }
}
