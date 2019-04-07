package nextstep.domain;

import nextstep.domain.entity.Answer;
import nextstep.domain.entity.Question;
import nextstep.domain.entity.User;
import nextstep.domain.repository.AnswerRepository;
import nextstep.domain.repository.QuestionRepository;
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
public class QnaRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    private long questionId;
    private long answerId;
    private User user;

    @Before
    public void setUp() throws Exception {
        this.user = new User(1L, "test", "password", "jpaTestMan", "jap@gmail.com");
        Question question = new Question("JPA Test", "for jpa test");
        question.writeBy(user);
        this.questionId = questionRepository.save(question).getId();

        Answer answer = new Answer(user, "Answer Contents");
        answerId = answerRepository.save(answer).getId();
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

    @Test
    public void add_Answer() {
        Question question = questionRepository.getOne(questionId);
        Answer answer = answerRepository.getOne(answerId);
        question.addAnswer(answer);
        questionRepository.save(question);
        long answerId = questionRepository.getOne(questionId).getAnswers().get(0).getId();

        Answer savedAnswer = answerRepository.getOne(answerId);
        assertThat(savedAnswer).isNotNull();
        assertThat(savedAnswer.getId()).isEqualTo(answerId);
    }

    @Test
    public void update_answer() {
        Question question = questionRepository.getOne(questionId);
        Answer answer = answerRepository.getOne(answerId);
        question.addAnswer(answer);
        questionRepository.save(question);
        long answerId = questionRepository.getOne(questionId).getAnswers().get(0).getId();

        Answer savedAnswer = answerRepository.getOne(answerId);
        savedAnswer.update(user, new Answer(user, "수정하겠습니다."));

        Answer updateAnswer = answerRepository.getOne(answerId);
        assertThat(updateAnswer.getContents()).isEqualTo("수정하겠습니다.");
    }
}