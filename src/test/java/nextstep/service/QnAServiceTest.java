package nextstep.service;

import nextstep.NotFoundException;
import nextstep.domain.*;
import org.junit.Before;
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
    private List<Question> questions = new ArrayList<>();
    private Question question = QuestionTest.newQuestion(1L);
    private List<Answer> answers = new ArrayList<>();
    private Answer answer = AnswerTest.newAnswer(1L);

    @Before
    public void setUp() {
        // question
        when(questionRepository.save(any(Question.class))).thenReturn(question);
        when(questionRepository.findByDeletedFalse()).thenReturn(questions);
        when(questionRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(question));

        // answer
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);
        when(answerRepository.findByQuestionAndDeletedFalse(question)).thenReturn(answers);
//        when(answerRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(answer));
    }

    @Test
    public void 질문을_등록한다() {
        QuestionBody questionBody = new QuestionBody("This is title", "This is contents");
        Question returned = qnaService.createQuestion(writer, questionBody);

        softly.assertThat(returned).isEqualTo(question);
    }

    @Test
    public void 질문_목록을_조회한다() {
        List<Question> returned = qnaService.findQuestions();

        softly.assertThat(returned).isEqualTo(questions);
    }

    @Test
    public void 질문을_상세조회한다() {
        Question returned = qnaService.findQuestionById(1L);

        softly.assertThat(returned).isEqualTo(question);
    }

    @Test(expected = NotFoundException.class)
    public void 없는_질문이면_예외가_발생한다() {
        qnaService.findQuestionById(100L);
    }

    @Test
    public void 질문을_수정한다() {
        QuestionBody newQuestionBody = new QuestionBody("This is updated title", "This is updated contents");
        Question returned = qnaService.updateQuestion(1L, writer, newQuestionBody);

        softly.assertThat(returned.getQuestionBody()).isEqualTo(newQuestionBody);
    }

    @Test
    public void 질문을_삭제한다() {
        qnaService.deleteQuestion(1L, writer);

        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test
    public void 답변을_등록한다() {
        User user = UserTest.newUser(2L);
        String contents = "This is fixtureAnswer";
        Answer returned = qnaService.addAnswer(user, 1L, contents);

        softly.assertThat(returned).isEqualTo(answer);
    }

    @Test
    public void 답변목록을_조회한다() {
        List<Answer> returned = qnaService.findAnswers(1L);

        softly.assertThat(returned).isEqualTo(answers);
    }
}
