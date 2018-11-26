package nextstep.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QuestionTest {

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "userRepository")
    private UserRepository userRepository;

    @Test
    public void 본인글_update() {
        Question question = questionRepository.findById(1L).get();
        User user = userRepository.findByUserId("javajigi").get();

        boolean result = question.update(user, new Question().setTitle("test title").setContents("test contents"));
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void 본인글아닌것_update() {
        Question question = questionRepository.findById(1L).get();
        User user = userRepository.findByUserId("sanjigi").get();

        boolean result = question.update(user, new Question().setTitle("test title").setContents("test contents"));
        assertThat(result).isEqualTo(false);
    }
}