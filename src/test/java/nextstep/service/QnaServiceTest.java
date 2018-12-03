package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import nextstep.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.List;
import java.util.Optional;

import static nextstep.domain.UserTest.JAVAJIGI;
import static nextstep.domain.UserTest.SANJIGI;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    private static final String TITLE = "테스트 타이틀";
    private static final String CONTENTS = "테스트 컨텐츠";
    private static final String ANSWER_CONTENTS = "테스트 답변 컨텐츠";

    private Question question;
    private Answer answer;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private DeleteHistoryRepository deleteHistoryRepository;

    @InjectMocks
    private QnaService qnaService;

    @Mock
    private DeleteHistoryService deleteHistoryService;

    @Before
    public void setup() {
        question = new Question(TITLE, CONTENTS);
        answer = new Answer(JAVAJIGI, ANSWER_CONTENTS);
    }

    @Test
    public void create() {
        when(questionRepository.save(question)).thenReturn(question);

        Question savedQuestion = qnaService.create(JAVAJIGI, question);
        softly.assertThat(savedQuestion).isEqualTo(question);
    }

    @Test
    public void update_writer(){
        question.writeBy(JAVAJIGI);
        Question updatedQuestion = new Question("수정 타이틀","수정 컨텐츠");

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.update(JAVAJIGI,1L,updatedQuestion);
        softly.assertThat(question).isEqualTo(updatedQuestion);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_writer_another_user(){
        question.writeBy(JAVAJIGI);
        Question updatedQuestion = new Question("수정 타이틀","수정 컨텐츠");

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.update(SANJIGI,1L,updatedQuestion);
    }

    @Test
    public void delete_writer() throws CannotDeleteException {
        question.writeBy(JAVAJIGI);
        when(questionRepository.findById(1l)).thenReturn(Optional.of(question));

        List<DeleteHistory> deleteHistories = qnaService.delete(JAVAJIGI, 1L);

        when(deleteHistoryRepository.findByDeletedBy(JAVAJIGI)).thenReturn(deleteHistories);

        softly.assertThat(question.isDeleted()).isTrue();
        softly.assertThat(deleteHistories.size()).isEqualTo(1);
        softly.assertThat(deleteHistoryRepository.findByDeletedBy(JAVAJIGI)).isEqualTo(deleteHistories);

    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_writer_another_user() throws CannotDeleteException {
        question.writeBy(JAVAJIGI);
        when(questionRepository.findById(1l)).thenReturn(Optional.of(question));

        qnaService.delete(SANJIGI,1L);
    }

    @Test
    public void findByIdWithAuthorized() {
        qnaService.create(JAVAJIGI, question);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        Question findQuestion = qnaService.findByIdWithAuthorized(JAVAJIGI, 1);

        softly.assertThat(findQuestion.getTitle()).isEqualTo(TITLE);
        softly.assertThat(findQuestion.getContents()).isEqualTo(CONTENTS);
    }

    @Test(expected = UnAuthorizedException.class)
    public void findByIdWithAuthorizedByAnotherUser() {
        User anotherUser = new User("mirrors89", "password", "name", "mirrors89@slipp.net");

        qnaService.create(JAVAJIGI, question);
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.findByIdWithAuthorized(anotherUser, 1);
    }

    @Test
    public void create_answer() {
        question.writeBy(JAVAJIGI);
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(answerRepository.save(answer)).thenReturn(answer);

        Answer savedAnswer = qnaService.addAnswer(JAVAJIGI, 1L, ANSWER_CONTENTS);
        softly.assertThat(savedAnswer).isEqualTo(answer);
    }

    @Test
    public void delete_answer_writer() throws CannotDeleteException {
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));

        Answer deleteAnswer = qnaService.deleteAnswer(JAVAJIGI, 1L);
        softly.assertThat(deleteAnswer.isDeleted()).isEqualTo(true);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_answer_another_user() throws CannotDeleteException {
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));

        qnaService.deleteAnswer(SANJIGI, 1L);
    }

    @Test
    public void delete_question_and_answer_writer_login() throws CannotDeleteException {
        question.writeBy(JAVAJIGI);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(answerRepository.save(answer)).thenReturn(answer);

        qnaService.addAnswer(JAVAJIGI, 1L, ANSWER_CONTENTS);

        List<DeleteHistory> deleteHistories = qnaService.delete(JAVAJIGI, 1L);
        when(deleteHistoryRepository.findByDeletedBy(JAVAJIGI)).thenReturn(deleteHistories);

        softly.assertThat(question.isDeleted()).isTrue();
        softly.assertThat(deleteHistoryRepository.findByDeletedBy(JAVAJIGI)).isEqualTo(deleteHistories);

    }

    @Test(expected = CannotDeleteException.class)
    public void delete_question_and_answer_another_login() throws CannotDeleteException {
        question.writeBy(SANJIGI);
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(answerRepository.save(answer)).thenReturn(answer);

        qnaService.addAnswer(JAVAJIGI, 1L, ANSWER_CONTENTS);
        qnaService.addAnswer(JAVAJIGI, 1L, ANSWER_CONTENTS);

        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));

        qnaService.deleteAnswer(SANJIGI, 1L);
    }

}
