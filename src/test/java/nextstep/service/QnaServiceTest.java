package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.CannotFoundException;
import nextstep.UnAuthorizedException;
import nextstep.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
    public final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private DeleteHistoryRepository deleteHistoryRepository;

    @InjectMocks
    private QnaService qnaService;

    @InjectMocks
    private DeleteHistoryService deleteHistoryService;


    private Question question;
    private Answer answer;

    @Before
    public void setUp(){
        question =  new Question(1L ,"질문 제목", "내용 블라", JAVAJIGI , new Answers());
        question.writeBy(JAVAJIGI);
        answer = new Answer(1L, SANJIGI, question, "답변 내용");
    }

    @Test
    public void 질문작성() {
        when(questionRepository.save(any())).thenReturn(question);

        Question savedQuestion = qnaService.createQuestion(question.getWriter(), question);

        assertThat(savedQuestion.isOwner(question.getWriter())).isTrue();
    }

    @Test
    public void 작성자_질문_수정() {
        when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));

        Question savedQuestion = qnaService.findByIdAndDeletedFalse(question.getWriter(), question.getId());

        assertThat(savedQuestion).isEqualTo(question);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 다른_작성자_질문_수정() {
        when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));

        Question savedQuestion = qnaService.findByIdAndDeletedFalse(SANJIGI, question.getId());

        assertThat(savedQuestion).isEqualTo(question);
    }

    @Test
    public void 작성자_질문_수정_내용() {
        when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));

        String title = "TDD";
        String contents = "리팩토링";
        Question updateQuestion = new Question(title, contents);
        qnaService.updateQuestion(question.getWriter(), question.getId(), updateQuestion);

        assertThat(question.getTitle()).isEqualTo(title);
        assertThat(question.getContents()).isEqualTo(contents);
    }

    @Test
    public void 작성자_질문_삭제() throws CannotDeleteException {
        when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));

        qnaService.deleteQuestion(JAVAJIGI, question.getId());

        assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 작성자_질문_삭제_다른사용자() throws CannotDeleteException {
        when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));

        qnaService.deleteQuestion(SANJIGI, question.getId());
    }

    @Test(expected = CannotDeleteException.class)
    public void 작성자_질문_삭제_이미_삭제() throws CannotDeleteException {
        when(questionRepository.findById(any())).thenReturn(Optional.ofNullable(question));
        question.setDeleted(Boolean.TRUE);
        qnaService.deleteQuestion(JAVAJIGI, question.getId());
    }

    @Test
    public void 답변작성() throws CannotFoundException {
        when(questionRepository.findByIdAndDeletedFalse(anyLong())).thenReturn(Optional.ofNullable(question));

        Answer saveAnswer = qnaService.addAnswer(JAVAJIGI, question.getId(), "리팩토링 합시다");

        assertThat(saveAnswer.isOwner(JAVAJIGI)).isTrue();
    }

    @Test
    public void 답변삭제() throws CannotDeleteException, CannotFoundException {
        when(questionRepository.findByIdAndDeletedFalse(anyLong())).thenReturn(Optional.ofNullable(question));
        when(answerRepository.findById(anyLong())).thenReturn(Optional.ofNullable(answer));

        Answer deleteAnswer = qnaService.deleteAnswer(SANJIGI, question.getId(), answer.getId());

        assertThat(deleteAnswer.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 답변삭제_다른사용자() throws CannotDeleteException, CannotFoundException {
        when(questionRepository.findByIdAndDeletedFalse(anyLong())).thenReturn(Optional.ofNullable(question));
        when(answerRepository.findById(anyLong())).thenReturn(Optional.ofNullable(answer));

        qnaService.deleteAnswer(JAVAJIGI, question.getId(), answer.getId());
    }

    @Test(expected = CannotDeleteException.class)
    public void 답변삭제_이미_삭제() throws Exception {
        when(questionRepository.findByIdAndDeletedFalse(anyLong())).thenReturn(Optional.ofNullable(question));
        when(answerRepository.findById(anyLong())).thenReturn(Optional.ofNullable(answer));

        qnaService.deleteAnswer(SANJIGI, question.getId(), answer.getId());
        qnaService.deleteAnswer(SANJIGI, question.getId(), answer.getId());
    }


}
