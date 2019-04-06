package nextstep.domain;


import nextstep.UnAuthenticationException;
import org.junit.Test;
import support.test.BaseTest;

import java.util.List;

public class QuestionTest extends BaseTest {
    private static final User LOGIN_USER = new User(1L, "nekisse", "password1", "nname", "nekisse@c.com");
    private static final String TITLE = "제목";
    private static final String CONTENTS = "내용";


    @Test(expected = UnAuthenticationException.class)
    public void 사용자가_다르면_글수정_불가능_익셉션() throws Exception {
        Question question = new Question(TITLE, CONTENTS);
        question.writeBy(LOGIN_USER);
        Question updateQuestion = new Question(question.getTitle() + "수정", question.getContents() + "수정");

        question.update(User.GUEST_USER, updateQuestion);
    }


    @Test
    public void 자신의_글_업데이트() throws Exception {
        Question question = new Question(TITLE, CONTENTS);
        question.writeBy(LOGIN_USER);
        Question updateQuestion = new Question(question.getTitle() + "수정", question.getContents() + "수정");

        question.update(LOGIN_USER, updateQuestion);

        softly.assertThat(question.getTitle()).isEqualTo(updateQuestion.getTitle());
        softly.assertThat(question.getContents()).isEqualTo("내용수정");
    }

    @Test
    public void 자신의_댓글없는_글_삭제() throws UnAuthenticationException {
        Question question = new Question(TITLE, CONTENTS);
        question.writeBy(LOGIN_USER);

        List<DeleteHistory> delete = question.delete(LOGIN_USER);

        softly.assertThat(delete.size()).isEqualTo(1);
    }

    @Test
    public void 자신의_글_자신의_댓글_2개_경우_삭제() throws UnAuthenticationException {
        Question question = new Question(TITLE, CONTENTS);
        question.writeBy(LOGIN_USER);
        Answer answer1 = new Answer(LOGIN_USER, "댓글1");
        question.addAnswer(answer1);

        List<DeleteHistory> delete = question.delete(LOGIN_USER);

        softly.assertThat(delete.size()).isEqualTo(2);

    }

    @Test(expected = UnAuthenticationException.class)
    public void 자신의_글_자신의_댓글_1개_다른유저_댓글1개_일때_경우_삭제불가능() throws UnAuthenticationException {
        Question question = new Question(TITLE, CONTENTS);
        question.writeBy(LOGIN_USER);
        Answer myAnswer = new Answer(LOGIN_USER, "댓글1");
        Answer otherUserAnswer = new Answer(User.GUEST_USER, "댓글2");
        question.addAnswer(myAnswer);
        question.addAnswer(otherUserAnswer);
        question.delete(LOGIN_USER);
    }
}