package nextstep.service;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void 질문_목록을_조회한다() {
        when(questionRepository.findAll()).thenReturn(Arrays.asList(
            new Question("This is title1", "This is contents1"),
            new Question("This is title2", "This is contents2"),
            new Question("This is title3", "This is contents3")));

        List<Question> list = qnaService.findAll();
        softly.assertThat(list).hasSize(3);
    }

    @Test
    public void 질문_상세를_조회한다() {
        when(questionRepository.findById(1L)).thenReturn(
            Optional.of(new Question("This is title", "This is contents")));

        Optional<Question> optionalQuestion = qnaService.findById(1L);
        softly.assertThat(optionalQuestion.isPresent()).isTrue(); // hmm...
    }

    @Test
    public void 질문을_등록한다() {
        User user = new User("myId", "myPassword", "myName", "myEmail");
        Question question = new Question("This is title", "This is content");

        qnaService.create(user, question);
        softly.assertThat(question.getWriter()).isEqualTo(user);
    }
}
