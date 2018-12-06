package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static nextstep.CannotDeleteException.EXISTED_ANOTHER_USER_ANSWER_EXCEPTION;
import static nextstep.domain.AnswerTest.newAnswer;
import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.SANJIGI;

public class QuestionTest extends BaseTest {

    public static final Question defaultQuestion = new Question("동해물과백두산이", "contents::동해물과백두산이");
    public static final Question updatedQuestion = new Question("변경_동해물과백두산이", "contents::변경_동해물과백두산이");

    public static Question newTestQuestion() {
        return newTestQuestion("동해물과백두산이", "contents::동해물과백두산이");
    }

    public static Question newTestQuestion(User user) {
        Question question = new Question("제목1", "내용1");
        question.writeBy(user);
        return question;
    }

    public static Question newTestQuestion(String title, String contents) {
        return new Question(title, contents);
    }

    public static Question newQuestionByDeleted() {
        Question question = new Question("제목", "내용", true);
        question.writeBy(JAVAJIGI);
        return question;
    }

    public static Question newQuestionHasAnswer(User loginUser, User answerCreateUser) {
        Answer answer = newAnswer(answerCreateUser, "동해물과백두산이");
        Question question = new Question("제목1", "내용1")
                .writeBy(loginUser);
        question.addAnswer(answer);
        return question;
    }

    @Test
    public void update_owner() {
        User loginUser = JAVAJIGI;
        Question origin = newTestQuestion(JAVAJIGI);
        Question target = newTestQuestion("제목", "내용");

        origin.update(loginUser, target);
        softly.assertThat(origin.getTitle()).isEqualTo(target.getTitle());
        softly.assertThat(origin.getContents()).isEqualTo(target.getContents());
    }

    @Test
    public void update_not_owner() throws Exception {
        exception.expect(UnAuthorizedException.class);

        Question origin = newTestQuestion(JAVAJIGI);
        User loginUser = SANJIGI;
        Question target = newTestQuestion("제목", "내용");

        origin.update(loginUser, target);
    }

    @Test
    public void delete_owner() throws Exception {
        User loginUser = JAVAJIGI;
        Question origin = newTestQuestion(loginUser);

        origin.delete(loginUser);
        softly.assertThat(origin.isDeleted()).isTrue();
    }

    @Test
    public void delete_not_owner() throws Exception {
        exception.expect(UnAuthorizedException.class);

        Question origin = newTestQuestion(JAVAJIGI);
        User loginUser = SANJIGI;

        origin.delete(loginUser);
    }

    @Test
    public void 다른_사람의_답변이_있는_경우_삭제가_불가능하다() throws Exception {
        exception.expect(CannotDeleteException.class);
        exception.expectMessage(EXISTED_ANOTHER_USER_ANSWER_EXCEPTION);

        User loginUser = JAVAJIGI;
        User answerCreateUser = SANJIGI;
        Question origin = newQuestionHasAnswer(loginUser, answerCreateUser);
        origin.delete(loginUser);
    }

    @Test
    public void 질문자와_답변_글의_모든_답변자_같은_경우_삭제가_가능하다() throws Exception {
        User loginUser = JAVAJIGI;
        Question origin = newQuestionHasAnswer(loginUser, loginUser);
        origin.delete(loginUser);
        softly.assertThat(origin.isDeleted()).isTrue();
    }

    @Test
    public void 삭제할_때_답변의_삭제상태_변경된다() throws Exception {
        User loginUser = JAVAJIGI;

        Answer answer = newAnswer(loginUser, "동해물과백두산이");
        Question origin = new Question("제목1", "내용1")
                .writeBy(loginUser);
        origin.addAnswer(answer);

        origin.delete(loginUser);
        softly.assertThat(origin.isDeleted()).isTrue();
        softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test
    public void 삭제시_이력을_남긴다() throws Exception {
        User loginUser = JAVAJIGI;
        Question origin = newTestQuestion(JAVAJIGI);
        DeleteHistory delete = origin.delete(loginUser);
        softly.assertThat(delete.getContentId()).isEqualTo(origin.getId());
    }
}
