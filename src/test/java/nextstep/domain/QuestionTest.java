package nextstep.domain;

import nextstep.UnAuthorizedException;
import nextstep.domain.entity.Answer;
import nextstep.domain.entity.Question;
import nextstep.domain.entity.User;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionTest {
    private Question question;
    private User user;

    @Before
    public void setUp() throws Exception {
        this.user = new User(1L, "test", "password", "jpaTestMan", "jap@gmail.com");
        Question question = new Question("JPA Test", "for jpa test");
        question.writeBy(user);
        this.question = question;
    }

    @Test
    public void update_with_owner() {
        Question newQuestion = new Question("수정된제목", "내용도 수정");
        this.question.modify(user, newQuestion);
        assertThat(this.question.getTitle()).isEqualTo(newQuestion.getTitle());
    }


    @Test(expected = UnAuthorizedException.class)
    public void update_with_wrong_user() {
        Question newQuestion = new Question("수정된제목", "내용도 수정");
        User loginUser = new User(2L, "bubble", "tea", "GongCha", "jap@gmail.com");
        this.question.modify(loginUser, newQuestion);
        assertThat(this.question.getTitle()).isEqualTo(newQuestion.getTitle());
        assertThat(this.question.getContents()).isEqualTo(newQuestion.getContents());
    }

    @Test
    public void delete_with_owner() {
        this.question.delete(user);
        assertThat(this.question.isDeleted()).isEqualTo(Boolean.TRUE);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_with_wrong_user() {
        User loginUser = new User(2L, "bubble", "tea", "GongCha", "jap@gmail.com");
        this.question.delete(loginUser);
    }

    @Test
    public void add_Answer() {
        question.addAnswer(new Answer(user, "안녕하세요!"));
        assertThat(question.getAnswers().size()).isEqualTo(1);
    }

    @Test
    public void delete_답변없는경우() {
        Question question = new Question(1L, "삭제대상", "지우겠습니다.", user);
        Question result = question.delete(user);
        assertThat(result.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_실패_다른사용자_댓글_존재() {
        Question question = new Question(1L, "삭제대상", "지우겠습니다.", user);
        User otherUser = new User(2L, "other", "other", "noname", "nono@gmail.com");
        Answer answer = new Answer(otherUser,"답변");
        question.addAnswer(answer);

        question.delete(user);
    }

    @Test
    public void delete_성공_질문_댓글_삭제() {
        Question question = new Question(1L, "삭제대상", "지우겠습니다.", user);
        Answer answer1 = new Answer(user,"답변");
        Answer answer2 = new Answer(user,"두번째");

        question.addAnswer(answer1);
        question.addAnswer(answer2);

        question.delete(user);
        assertThat(question.isDeleted()).isTrue();
        for (Answer answer : question.getAnswers()) {
            assertThat(answer.isDeleted()).isTrue();
        }
    }
}