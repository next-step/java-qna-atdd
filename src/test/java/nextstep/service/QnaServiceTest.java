package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import nextstep.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    private User javajigi = new User("javajigi", "password", "name", "javajigi@slipp.net");

    @Test
    public void create(){
        Question question = new Question("test_title","test_contents");
        when(questionRepository.save(question)).thenReturn(question);

        Question question_saved = qnaService.create(javajigi,question);
        softly.assertThat(question_saved).isEqualTo(question);
    }

    @Test
    public void update_Owner(){
        Question updatedQ = new Question("update_title","update_contents");
        Question question = new Question("testQ","testQ");
        question.writeBy(javajigi);
        when(questionRepository.findById(1l)).thenReturn(Optional.of(question));
        qnaService.update(javajigi,1l,updatedQ);
        softly.assertThat(question).isEqualTo(updatedQ);
    }

    @Test
    public void delete_Owner() throws CannotDeleteException {
        Question question = new Question("testQ","testQ");
        question.writeBy(javajigi);
        when(questionRepository.findById(1l)).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(javajigi,1L);
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test
    public void addAnswer(){
        Question question = new Question("testQ","testQ");
        question.writeBy(javajigi);
        Answer resultanswer = new Answer(javajigi,"updateAnswer");

        when(questionRepository.findById(1l)).thenReturn(Optional.of(question));
        when(answerRepository.save(resultanswer)).thenReturn(resultanswer);
        Answer answer = qnaService.addAnswer(javajigi,1L,"updateAnswer");

        softly.assertThat(answer.getContents()).contains("update");
    }

    @Test
    public void showAnswer(){
        Answer answer = new Answer(javajigi,"contents");
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));
        Answer result = qnaService.showAnswer(1L);

        softly.assertThat(result.getContents()).contains("contents");

    }
}
