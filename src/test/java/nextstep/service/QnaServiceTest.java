package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.UnAuthenticationException;
import nextstep.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static nextstep.domain.Fixture.mockQuestion;
import static nextstep.domain.Fixture.mockUser;

import static org.mockito.Mockito.when;

// TODO : 중복데이터 추출 필요
@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    private Optional<Question> returnCacheValue;
    private Optional<Answer> returnCacheAnswer;

    @Before
    public void setUp() {
        mockQuestion.writeBy(mockUser);
        Fixture.answer.setId(0);
        returnCacheValue = Optional.of((Question) mockQuestion);
        returnCacheAnswer = Optional.of((Answer) Fixture.answer);
    }

    @Test
    public void 생성테스트() {
        when(questionRepository.save(mockQuestion)).thenReturn(mockQuestion);

        Question result = qnaService.create(mockUser, mockQuestion);
        softly.assertThat(result).isEqualTo(mockQuestion);
        softly.assertThat(result.getWriter()).isNotNull();
    }

    @Test
    public void 업데이트_테스트() throws Exception {
        when(questionRepository.findById(new Long(0))).thenReturn(returnCacheValue);

        Question result = qnaService.update(new User("sanjigi", "password", "name", "javajigi@slipp.net"), 0, new Question("아기상어", "아빠상어"));
        softly.assertThat(result.getContents()).isEqualTo("뚜루루뚜루");
        softly.assertThat(result.getTitle()).isEqualTo("아빠상어");
        softly.assertThat(result.getWriter()).isNotNull();
    }

    @Test(expected = UnAuthenticationException.class)
    public void 업데이트_실패_테스트() throws Exception {
        Question test = new Question(Fixture.title, Fixture.contents);
        test.writeBy(mockUser);
        returnCacheValue = Optional.of((Question) test);
        when(questionRepository.findById(new Long(1))).thenReturn(returnCacheValue);

        Question result = qnaService.update(new User("이상하네", "아무리봐도", "다른데", "왜그래@slipp.net"), 1, mockQuestion);
        softly.assertThat(result.getContents()).isEqualTo("뚜루루뚜루");
        softly.assertThat(result.getTitle()).isEqualTo("아기상어");
        softly.assertThat(result.getWriter()).isNotNull();
    }


    @Test
    public void 삭제_테스트() throws CannotDeleteException {
        when(questionRepository.findById(new Long(0))).thenReturn(returnCacheValue);
        
        qnaService.deleteQuestion(new User("sanjigi", "password", "name", "javajigi@slipp.net"), 0);
    }

    @Test(expected = EntityNotFoundException.class)
    public void 삭제_실패_테스트() throws CannotDeleteException {
        when(questionRepository.findById(new Long(0))).thenReturn(Optional.empty());

        qnaService.deleteQuestion(new User("sanjigi2", "password", "name", "javajigi@slipp.net"), 0);
    }

    @Test
    public void 답변_추가_테스트() {
        when(questionRepository.findById(new Long(0))).thenReturn(returnCacheValue);

        Answer result = qnaService.addAnswer(new User("sanjigi", "password", "name", "javajigi@slipp.net"), 0, "엄마상어는요!");
        softly.assertThat(result.getContents()).isEqualTo("엄마상어는요!");
    }

    @Test
    public void 답변_삭제_테스트() throws CannotDeleteException {
        when(answerRepository.findById(new Long(0))).thenReturn(returnCacheAnswer);

        qnaService.deleteAnswer(new User("sanjigi", "password", "name", "javajigi@slipp.net"), 0);
    }

    @Test
    public void 답변_삭제_실패_테스트() throws CannotDeleteException {
        when(answerRepository.findById(new Long(0))).thenReturn(returnCacheAnswer);

        qnaService.deleteAnswer(new User("sanjigi2", "password", "name", "javajigi@slipp.net"), 0);
    }
}