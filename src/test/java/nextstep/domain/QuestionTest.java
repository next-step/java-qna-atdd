package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class QuestionTest {
    private final Logger log = LoggerFactory.getLogger(QuestionTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    private Long questionId;

    private User user;

    @Before
    public void setUp() throws Exception {
        this.user = new User(1L, "test", "password", "jpaTestMan", "jap@gmail.com");
        Question question = new Question("JPA Test", "for jpa test");
        question.writeBy(user);
        this.questionId = questionRepository.save(question).getId();
    }

    @After
    public void tearDown() throws Exception {
        questionRepository.deleteById(questionId);
    }

    @Test
    public void update_with_no_save() {
        Question question = questionRepository.findById(questionId).get();
        question.modify(user, new Question("title", "modify"));

        log.debug("entityFromDB : {}", questionRepository.findById(questionId).get().getTitle());
        log.debug("updatedEntity : {}", question.getTitle());
        assertThat(question.getTitle()).isEqualTo(questionRepository.findById(questionId).get().getTitle());
        assertThat(question).isEqualTo(questionRepository.findById(questionId).get());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_with_wrong_user() {
        Question updatedQuestion = new Question("수정된제목", "내용도 수정");
        User loginUser = new User(2L, "bubble", "tea", "GongCha", "jap@gmail.com");

        Question question = questionRepository.findById(questionId).get();
        question.modify(loginUser, updatedQuestion);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_with_wrong_user() throws CannotDeleteException {
        User loginUser = new User(2L, "bubble", "tea", "GongCha", "jap@gmail.com");
        Question question = questionRepository.findById(questionId).get();
        question.delete(loginUser);
    }
}