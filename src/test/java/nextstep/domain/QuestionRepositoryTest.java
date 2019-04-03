package nextstep.domain;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class QuestionRepositoryTest {

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

        assertThat(question.getTitle()).isEqualTo(questionRepository.findById(questionId).get().getTitle());
        assertThat(question).isEqualTo(questionRepository.findById(questionId).get());
    }
}