package nextstep.service;

import nextstep.ForbiddenException;
import nextstep.NotFoundException;
import nextstep.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Arrays;
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
        QuestionBody questionBody = new QuestionBody("This is title", "This is contents");
        질문_1건을_저장_가능하게함(questionBody);

        Question returned = qnaService.createQuestion(writer, questionBody);

        softly.assertThat(returned.getQuestionBody()).isEqualTo(questionBody);
    }

    @Test
    public void 질문_목록을_조회한다() {
        질문_목록을_조회_가능하게함();

        List<Question> returned = qnaService.findQuestions();

        softly.assertThat(returned).isNotEmpty();
    }

    @Test
    public void 질문을_상세조회한다() {
        질문_1건을_조회_가능하게함(1L);

        Question returned = qnaService.findQuestionById(1L);

        softly.assertThat(returned.getId()).isEqualTo(1L);
    }

    @Test(expected = NotFoundException.class)
    public void 없는_질문이면_예외가_발생한다() {
        qnaService.findQuestionById(100L);
    }

    @Test
    public void 질문을_수정한다() {
        질문_1건을_조회_가능하게함(1L);

        QuestionBody newQuestionBody = new QuestionBody("This is updated title", "This is updated contents");
        Question returned = qnaService.updateQuestion(1L, writer, newQuestionBody);

        softly.assertThat(returned.getQuestionBody()).isEqualTo(newQuestionBody);
    }

    @Test(expected = ForbiddenException.class)
    public void 다른_사용자가_등록한_답변이_있을경우_질문삭제가_불가능하다() {
        답변이_포함된_질문_1건을_조회_가능하게함_$질문자와_답변자가_다름$(1L);

        qnaService.deleteQuestion(1L, writer);
    }

    @Test
    public void 답변을_등록한다() {
        질문_1건을_조회_가능하게함(1L);

        String contents = "This is answer";
        Answer returned = qnaService.addAnswer(writer, 1L, contents);

        softly.assertThat(returned.getContents()).isEqualTo(contents);
    }

    @Test
    public void 답변목록을_조회한다() {
        답변이_포함된_질문_1건을_조회_가능하게함_$질문자와_답변자가_같음$(1L);

        List<Answer> returned = qnaService.findAnswers(1L);

        softly.assertThat(returned).isNotEmpty();
    }

    @Test
    public void 답변을_조회한다() {
        답변_1건_조회를_가능하게함(1L, 1L);

        Answer returned = qnaService.findAnswer(1L);

        softly.assertThat(returned.getId()).isEqualTo(1L);
    }

    @Test
    public void 답변을_삭제한다() {
        답변_1건_조회를_가능하게함(1L, 1L);

        Answer answer = qnaService.deleteAnswer(writer, 1L);

        softly.assertThat(answer.isDeleted()).isTrue();
    }

    private void 질문_1건을_저장_가능하게함(QuestionBody questionBody) {
        Question question = new Question(writer, questionBody);
        when(questionRepository.save(any(Question.class))).thenReturn(question);
    }

    private void 질문_목록을_조회_가능하게함() {
        List<Question> questions = Arrays.asList(QuestionTest.newQuestion(1L), QuestionTest.newQuestion(2L));
        when(questionRepository.findByDeletedFalse()).thenReturn(questions);
    }

    private void 질문_1건을_조회_가능하게함(Long questionId) {
        Question question = QuestionTest.newQuestion(questionId);
        when(questionRepository.findByIdAndDeletedFalse(questionId)).thenReturn(Optional.of(question));
    }

    private void 답변이_포함된_질문_1건을_조회_가능하게함_$질문자와_답변자가_같음$(Long questionId) {
        Question question = QuestionTest.newQuestion(questionId);
        question.addAnswer(new Answer(writer, QuestionTest.newQuestion(questionId), "answer"));
        when(questionRepository.findByIdAndDeletedFalse(questionId)).thenReturn(Optional.of(question));
    }

    private void 답변이_포함된_질문_1건을_조회_가능하게함_$질문자와_답변자가_다름$(Long questionId) {
        Question question = QuestionTest.newQuestion(questionId);
        question.addAnswer(new Answer(UserTest.newUser(2L), QuestionTest.newQuestion(questionId), "answer"));
        when(questionRepository.findByIdAndDeletedFalse(questionId)).thenReturn(Optional.of(question));
    }

    private void 답변_1건_조회를_가능하게함(Long questionId, Long answerId) {
        답변이_포함된_질문_1건을_조회_가능하게함_$질문자와_답변자가_같음$(questionId);

        Answer answer = AnswerTest.newAnswer(answerId);
        when(answerRepository.findByIdAndDeletedFalse(answerId)).thenReturn(Optional.of(answer));
    }
}
