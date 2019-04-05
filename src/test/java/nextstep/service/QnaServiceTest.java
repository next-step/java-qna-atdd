package nextstep.service;

import nextstep.domain.Answer;
import nextstep.domain.AnswerRepository;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.exception.CannotDeleteException;
import nextstep.exception.ObjectDeletedException;
import nextstep.exception.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class QnaServiceTest extends BaseTest {
    private static final long ID_ONE = 1L;
    private static final long ID_TWO = 2L;

    private User loginUser;
    private Question question;
    private User writer;
    private Answer answer;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    @Before
    public void setUp() throws Exception {
        this.loginUser = new User("namjunemy", "1234", "njkim", "njkim@slipp.net");
        this.question = new Question("제목입니다", "내용입니다");
        this.writer = new User("nj", "test", "nj", "nj@slipp.net");
        this.answer = new Answer(writer, "답변 내용");
    }

    @Test
    public void 질문_조회() throws Exception {
        question.writeBy(loginUser);
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        Question findQuestion = qnaService.findQuestionById(loginUser, question.getId());
        softly.assertThat(findQuestion).isEqualTo(question);
    }

    @Test(expected = EntityNotFoundException.class)
    public void 질문_조회_EntityNotFoundException() {
        question.writeBy(loginUser);

        when(questionRepository.findById(question.getId())).thenReturn(Optional.empty());

        qnaService.findQuestionById(loginUser, question.getId());
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문_조회_UnAuthorizedException() {
        loginUser.setId(ID_TWO);
        writer.setId(ID_ONE);

        Question question = new Question("제목입니다", "내용입니다");
        question.writeBy(writer);

        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        qnaService.findQuestionById(loginUser, question.getId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void 질문_업데이트_EntityNotFoundException() {
        loginUser.setId(ID_TWO);
        writer.setId(ID_ONE);

        question.writeBy(writer);

        //then
        qnaService.updateQuestion(loginUser, ID_TWO, question);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문_업데이트_UnAuthorizedException() {
        loginUser.setId(ID_TWO);
        writer.setId(ID_ONE);

        question.writeBy(writer);

        when(questionRepository.findById(ID_ONE)).thenReturn(Optional.of(question));

        qnaService.updateQuestion(loginUser, ID_ONE, question);
    }

    @Test(expected = ObjectDeletedException.class)
    public void 질문_업데이트_ObjectDeletedException() {
        question.writeBy(loginUser);

        when(questionRepository.findById(ID_ONE)).thenReturn(Optional.of(question));
        qnaService.deleteQuestion(loginUser, ID_ONE);

        qnaService.updateQuestion(loginUser, ID_ONE, new Question("제목 수정", "내용 수정"));
    }

    @Test
    public void 질문_업데이트_성공() {
        question.writeBy(loginUser);

        when(questionRepository.findById(ID_ONE)).thenReturn(Optional.of(question));

        qnaService.updateQuestion(loginUser, ID_ONE, new Question("제목 수정", "내용 수정"));
        softly.assertThat(question.getTitle()).isEqualTo("제목 수정");
        softly.assertThat(question.getContents()).isEqualTo("내용 수정");
    }

    @Test(expected = EntityNotFoundException.class)
    public void 질문_삭제_EntityNotFoundException() {
        question.writeBy(writer);

        //then
        qnaService.deleteQuestion(loginUser, ID_TWO);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문_삭제_UnAuthorizedException() {
        loginUser.setId(ID_TWO);
        writer.setId(ID_ONE);

        question.writeBy(writer);

        when(questionRepository.findById(ID_ONE)).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(loginUser, ID_ONE);
    }

    @Test(expected = ObjectDeletedException.class)
    public void 질문_삭제_ObjectDeletedException() {
        question.writeBy(loginUser);

        when(questionRepository.findById(ID_ONE)).thenReturn(Optional.of(question));
        qnaService.deleteQuestion(loginUser, ID_ONE);

        qnaService.deleteQuestion(loginUser, ID_ONE);
    }

    @Test(expected = CannotDeleteException.class)
    public void 질문_삭제_CannotDeleteException() {
        question.writeBy(loginUser);

        //질문 존재 삭제 불가
        question.addAnswer(answer);

        when(questionRepository.findById(ID_ONE)).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(loginUser, ID_ONE);
    }

    @Test
    public void 질문_삭제_성공() {
        question.writeBy(loginUser);

        when(questionRepository.findById(ID_ONE)).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(loginUser, ID_ONE);
        assertTrue(question.isDeleted());
    }

    @Test
    public void 답변_조회() {
        question.writeBy(loginUser);

        answer.setId(ID_ONE);
        question.addAnswer(answer);

        when(questionRepository.findById(ID_ONE)).thenReturn(Optional.of(question));
        when(answerRepository.findById(ID_ONE)).thenReturn(Optional.of(answer));

        Answer savedAnswer = qnaService.findAnswerById(ID_ONE, ID_ONE);
        softly.assertThat(savedAnswer).isEqualTo(answer);
    }

    @Test(expected = EntityNotFoundException.class)
    public void 답변_조회_EntityNotFoundException() {
        question.writeBy(loginUser);

        answer.setId(ID_ONE);
        question.addAnswer(answer);

        when(questionRepository.findById(ID_ONE)).thenReturn(Optional.of(question));
        when(answerRepository.findById(ID_ONE)).thenReturn(Optional.of(answer));

        Answer savedAnswer = qnaService.findAnswerById(ID_ONE, ID_TWO);
    }

    @Test
    public void 답변_수정() {
        question.writeBy(loginUser);

        answer.setId(ID_ONE);
        question.addAnswer(answer);

        when(questionRepository.findById(ID_ONE)).thenReturn(Optional.of(question));
        when(answerRepository.findById(ID_ONE)).thenReturn(Optional.of(answer));

        Answer newAnswer = new Answer(answer.getId(), answer.getWriter(), answer.getQuestion(), "답변 수정");
        Answer updatedAnswer = answer.update(writer, newAnswer);

        softly.assertThat(updatedAnswer.getContents()).isEqualTo("답변 수정");
    }

    @Test(expected = UnAuthorizedException.class)
    public void 답변_수정_UnAuthorizedException() {
        loginUser.setId(ID_ONE);

        question.writeBy(loginUser);

        answer.setId(ID_ONE);
        question.addAnswer(answer);

        when(questionRepository.findById(ID_ONE)).thenReturn(Optional.of(question));
        when(answerRepository.findById(ID_ONE)).thenReturn(Optional.of(answer));

        Answer newAnswer = new Answer(answer.getId(), answer.getWriter(), answer.getQuestion(), "답변 수정");
        Answer updatedAnswer = answer.update(loginUser, newAnswer);
    }

    @Test(expected = ObjectDeletedException.class)
    public void 답변_수정_ObjectDeletedException() {
        loginUser.setId(ID_ONE);

        question.writeBy(loginUser);

        answer.setId(ID_ONE);
        question.addAnswer(answer);

        when(questionRepository.findById(ID_ONE)).thenReturn(Optional.of(question));
        when(answerRepository.findById(ID_ONE)).thenReturn(Optional.of(answer));

        answer.delete(writer);

        Answer newAnswer = new Answer(answer.getId(), answer.getWriter(), answer.getQuestion(), "답변 수정");
        Answer updatedAnswer = answer.update(loginUser, newAnswer);
    }

    @Test
    public void 답변_삭제() {
        question.writeBy(loginUser);

        answer.setId(ID_ONE);
        question.addAnswer(answer);

        when(questionRepository.findById(ID_ONE)).thenReturn(Optional.of(question));
        when(answerRepository.findById(ID_ONE)).thenReturn(Optional.of(answer));

        answer.delete(writer);

        assertTrue(answer.isDeleted());
    }

    @Test(expected = UnAuthorizedException.class)
    public void 답변_삭제_UnAuthorizedException() {
        question.writeBy(loginUser);
        answer.setId(ID_ONE);

        writer.setId(ID_TWO);
        Answer answer = new Answer(writer, "답변 내용");
        question.addAnswer(answer);

        when(questionRepository.findById(ID_ONE)).thenReturn(Optional.of(question));
        when(answerRepository.findById(ID_ONE)).thenReturn(Optional.of(answer));

        answer.delete(loginUser);
    }

    @Test(expected = ObjectDeletedException.class)
    public void 답변_삭제_ObjectDeletedException() {
        question.writeBy(loginUser);

        answer.setId(ID_ONE);
        question.addAnswer(answer);

        when(questionRepository.findById(ID_ONE)).thenReturn(Optional.of(question));
        when(answerRepository.findById(ID_ONE)).thenReturn(Optional.of(answer));

        answer.delete(writer);
        answer.delete(writer);
    }
}
