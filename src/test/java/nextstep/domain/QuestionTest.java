package nextstep.domain;


import nextstep.UnAuthenticationException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    private static final User LOGIN_USER = new User(1L, "nekisse", "password1", "nname", "nekisse@c.com");
    private static final String TITLE = "제목";
    private static final String CONTENTS = "내용";


    @Test(expected = UnAuthenticationException.class)
    public void 사용자가_다르면_익셉션() throws Exception {
        Question question = new Question(TITLE, CONTENTS);
        question.writeBy(LOGIN_USER);
        Question updateQuestion = new Question(question.getTitle() + "수정", question.getContents() + "수정");
        question.update(User.GUEST_USER, updateQuestion);
    }


    @Test
    public void 업데이트() throws Exception {
        Question question = new Question(TITLE, CONTENTS);
        question.writeBy(LOGIN_USER);
        Question updateQuestion = new Question(question.getTitle() + "수정", question.getContents() + "수정");
        question.update(LOGIN_USER, updateQuestion);
        softly.assertThat(question.getTitle()).isEqualTo(updateQuestion.getTitle());
        softly.assertThat(question.getContents()).isEqualTo("내용수정");

    }
}