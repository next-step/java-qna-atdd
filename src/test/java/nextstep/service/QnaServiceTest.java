package nextstep.service;

import nextstep.UnAuthorizedException;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    private Question basicQuestion;

    @Before
    public void setup() {
        basicQuestion = new Question("타이틀", "컨텐츠", basicUser);
    }

    @Test(expected = UnAuthorizedException.class)
    public void getAuthorizedQuestionWithoutAuthorized() {
        when(questionRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(basicQuestion));

        qnaService.findByIdWithAuthorized(anotherUser, 0);
    }

    public void getAuthorizedQuestion() {
        when(questionRepository.findById(0L)).thenReturn(Optional.of(basicQuestion));

        Question question = qnaService.findByIdWithAuthorized(anotherUser, 0);

        softly.assertThat(question.getTitle()).isEqualTo(basicQuestion.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(basicQuestion.getContents());
    }
}
