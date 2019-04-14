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

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    private static final Question ORIGINAL_QUESTION = new Question("title", "contents");
    private static final Question UPDATE_QUESTION = new Question("update title", "update contents");
    private static final Answer ORIGIN_ANSWER = new Answer(1L, UserTest.JAVAJIGI, ORIGINAL_QUESTION, "answer");
    private static final Answer UPDATE_ANSWER = new Answer(1L, UserTest.JAVAJIGI, ORIGINAL_QUESTION, "update question");

    @Before
    public void setUp() {
        when(this.questionRepository.findById(1L)).thenReturn(Optional.of(ORIGINAL_QUESTION));
        when(this.answerRepository.findById(1L)).thenReturn(Optional.of(ORIGIN_ANSWER));
    }

    @Before
    public void initQnaService() {
        ORIGINAL_QUESTION.writeBy(UserTest.JAVAJIGI);

    }

    @Test
    public void update_owner() {
        this.qnaService.update(UserTest.JAVAJIGI,1L, UPDATE_QUESTION);
        softly.assertThat(ORIGINAL_QUESTION.getTitle()).isEqualTo(UPDATE_QUESTION.getTitle());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() throws UnAuthorizedException{
        this.qnaService.update(UserTest.SANJIGI,1L, UPDATE_QUESTION);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_guest() throws UnAuthorizedException{
        this.qnaService.update(User.GUEST_USER,1L, UPDATE_QUESTION);
    }

    @Test
    public void delete_owner() throws CannotDeleteException {
        this.qnaService.deleteQuestion(UserTest.JAVAJIGI, 1L);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_owner() throws CannotDeleteException {
        this.qnaService.deleteQuestion(UserTest.SANJIGI, 1L);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_guest() throws CannotDeleteException {
        this.qnaService.deleteQuestion(User.GUEST_USER, 1L);
    }

    @Test
    public void create() {
        this.qnaService.addAnswer(UserTest.JAVAJIGI, 1L, ORIGIN_ANSWER);
    }

    @Test(expected = EntityNotFoundException.class)
    public void create_not_exist_question() {
        this.qnaService.addAnswer(UserTest.JAVAJIGI, 10L, ORIGIN_ANSWER);
    }


    @Test
    public void detail_answer() throws CannotDeleteException {
        Answer answer = this.qnaService.deleteAnswer(UserTest.JAVAJIGI, 1L);
        assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_answer_not_owner() throws CannotDeleteException {
        this.qnaService.deleteAnswer(UserTest.SANJIGI, 1L);
    }
}
