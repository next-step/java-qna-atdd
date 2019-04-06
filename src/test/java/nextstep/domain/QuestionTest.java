package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionTest {
    //TODO : Question과 Answers에 대한 관계 테스트

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
}