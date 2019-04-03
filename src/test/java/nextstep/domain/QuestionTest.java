package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

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

    @Test(expected = UnAuthorizedException.class)
    public void update_with_wrong_user() {
        Question updatedQuestion = new Question("수정된제목", "내용도 수정");
        User loginUser = new User(2L, "bubble", "tea", "GongCha", "jap@gmail.com");
        this.question.modify(loginUser, updatedQuestion);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_with_wrong_user() throws CannotDeleteException {
        User loginUser = new User(2L, "bubble", "tea", "GongCha", "jap@gmail.com");
        this.question.delete(loginUser);
    }
}