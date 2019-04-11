package nextstep.service;

import nextstep.NotFoundException;
import nextstep.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class QnAServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnAService qnaService;

    private User writer = UserTest.newUser(1L);

    @Test
    public void 질문을_등록한다() {
        Question question = QuestionTest.newQuestion(1L);
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        QuestionBody questionBody = new QuestionBody("This is title", "This is contents");
        Question returned = qnaService.createQuestion(writer, questionBody);

        softly.assertThat(returned).isEqualTo(question);
    }

    @Test
    public void 질문_목록을_조회한다() {
        List<Question> questions = new ArrayList<>();
        when(questionRepository.findByDeletedFalse()).thenReturn(questions);

        List<Question> returned = qnaService.findQuestions();

        softly.assertThat(returned).isEqualTo(questions);
    }

    @Test
    public void 질문을_상세조회한다() {
        Question question = QuestionTest.newQuestion(1L);
        when(questionRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(question));

        Question returned = qnaService.findQuestionById(1L);

        softly.assertThat(returned).isEqualTo(question);
    }

    @Test(expected = NotFoundException.class)
    public void 없는_질문이면_예외가_발생한다() {
//        when(questionRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        qnaService.findQuestionById(100L);
    }

    @Test
    public void 질문을_수정한다() {
        Question question = QuestionTest.newQuestion(1L);
        when(questionRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(question));

        QuestionBody newQuestionBody = new QuestionBody("This is updated title", "This is updated contents");
        Question returned = qnaService.updateQuestion(1L, writer, newQuestionBody);

        softly.assertThat(returned.getQuestionBody()).isEqualTo(newQuestionBody);
    }

//    @Test(expected = ForbiddenException.class)
//    public void 다른_사용자가_등록한_답변이_있을경우_질문삭제가_불가능하다() {
//        User user = UserTest.newUser(2L);
//        List<Answer> answers = Arrays.asList(AnswerTest.newAnswer(1L, user));
//        when(answerRepository.findByQuestionAndDeletedFalse(question)).thenReturn(answers);
//
//        qnaService.deleteQuestion(1L, writer);
//    }

    @Test
    public void 답변을_등록한다() {
        Question question = QuestionTest.newQuestion(1L);
        when(questionRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(question));
        Answer answer = AnswerTest.newAnswer(1L);
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        User user = UserTest.newUser(2L);
        String contents = "This is fixtureAnswer";
        Answer returned = qnaService.addAnswer(user, 1L, contents);

        softly.assertThat(returned).isEqualTo(answer);
    }

    @Test
    public void 답변목록을_조회한다() {
        Question question = QuestionTest.newQuestion(1L);
        when(questionRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(question));
        List<Answer> answers = new ArrayList<>();
        when(answerRepository.findByQuestionAndDeletedFalse(question)).thenReturn(answers);

        List<Answer> returned = qnaService.findAnswers(1L);

        softly.assertThat(returned).isEqualTo(answers);
    }

    @Test
    public void 답변을_조회한다() {
        Answer answer = AnswerTest.newAnswer(1L);
        when(answerRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(answer));

        Answer returned = qnaService.findAnswer(1L);

        softly.assertThat(returned).isEqualTo(answer);
    }

    @Test
    public void 답변을_삭제한다() {
        Answer answer = AnswerTest.newAnswer(1L);
        when(answerRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(answer));

        qnaService.deleteAnswer(writer, 1L);

        softly.assertThat(answer.isDeleted()).isTrue();
    }
}
