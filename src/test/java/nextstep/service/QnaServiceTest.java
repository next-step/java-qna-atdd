package nextstep.service;

import nextstep.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import support.test.BaseTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void create() {
        Question question = new Question("질문하기", "테스트");
        User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        when(questionRepository.save(question)).thenReturn(question);
        Question result = qnaService.create(user, question);
        softly.assertThat(result).isEqualTo(question);
    }

    @Test
    public void update() {
        Question question = new Question("질문하기", "테스트");
        User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        question.writeBy(user);
        when(questionRepository.save(question)).thenReturn(question);
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        Question result = qnaService.update(user, question.getId(), question);
        softly.assertThat(result).isEqualTo(question);
    }

    @Test
    public void findAll() {
        Question question = new Question("질문하기", "질문하기 테스트");
        List<Question> questions = new ArrayList<>();
        questions.add(question);
        when(questionRepository.findByDeleted(false)).thenReturn(questions);
        Iterable<Question> result = qnaService.findAll();
        softly.assertThat(result).size().isEqualTo(1);
    }

    @Test
    public void findAllWithPageable() {
        Question question = new Question("질문하기", "질문하기 테스트");
        List<Question> questions = new ArrayList<>();
        questions.add(question);
        Page<Question> page = new PageImpl<>(questions);

        PageRequest pageRequest = PageRequest.of(5, 10);
        when(questionRepository.findAll(pageRequest)).thenReturn(page);
        List<Question> result = qnaService.findAll(pageRequest);
        softly.assertThat(result).isEqualTo(questions);
    }

    @Test
    public void findById() {
        Question question = new Question("질문하기", "질문하기 테스트");
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        Question result = qnaService.findById(question.getId());
        softly.assertThat(result).isEqualTo(question);
    }

    @Test
    public void deleteQuestion() {
        Question question = new Question("질문하기", "테스트");
        User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        question.writeBy(user);
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        qnaService.deleteQuestion(user, question.getId());
        verify(questionRepository).delete(question);
    }

    @Test
    public void addAnswer() {
        Question question = new Question("질문하기", "테스트");
        User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        Answer answer = new Answer(user, "답변");
        answer.toQuestion(question);
        when(answerRepository.save(answer)).thenReturn(answer);
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        Answer result = qnaService.addAnswer(user, question.getId(), "답변");
        softly.assertThat(result).isEqualTo(answer);
    }

    @Test
    public void deleteAnswer() {
        User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        Answer answer = new Answer(user, "contents");
        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));
        qnaService.deleteAnswer(user, answer.getId());
        verify(answerRepository).delete(answer);
    }
}