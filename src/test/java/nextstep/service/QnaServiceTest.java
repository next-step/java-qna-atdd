package nextstep.service;

import nextstep.UnAuthenticationException;
import nextstep.UnAuthorizedException;
import nextstep.domain.*;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nextstep.domain.QuestionTest.DEFAULT_QUESTION;
import static nextstep.domain.UserTest.JAVAJIGI;
import static org.mockito.Mockito.*;

public class QnaServiceTest extends BaseTest {

    private QnaService qnaService;

    private QuestionRepository questionRepository = mock(QuestionRepository.class);
    private AnswerRepository answerRepository = mock(AnswerRepository.class);
    private DeleteHistoryService deleteHistoryService = mock(DeleteHistoryService.class);


    @Before
    public void setup() {
        this.qnaService = new QnaService(questionRepository, answerRepository, deleteHistoryService);
    }

    @Test
    public void findById() {
        final Long questionId = 1L;
        final Question question = new Question("title", "contents");
        final Optional<Question> expected = Optional.of(question);
        when(questionRepository.findById(questionId)).thenReturn(expected);


        final Optional<Question> questionOptional = qnaService.findById(questionId);


        softly.assertThat(questionOptional).isEqualTo(expected);
    }

    @Test
    public void findNotDeletedQuestionById() {
        final Long questionId = 1L;
        final Question question = new Question("title", "contents");
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(question));


        final Question findedQuestion = qnaService.findNotDeletedQuestionById(questionId);


        softly.assertThat(findedQuestion).isEqualTo(question);
    }

    @Test
    public void create() throws UnAuthenticationException {
        final Question mockedQuestion = mock(Question.class);
        when(questionRepository.save(mockedQuestion)).thenReturn(mockedQuestion);

        final Question created = qnaService.create(JAVAJIGI, mockedQuestion);

        verify(mockedQuestion, times(1)).writeBy(JAVAJIGI);
        softly.assertThat(mockedQuestion).isEqualTo(created);
    }

    @Test(expected = UnAuthenticationException.class)
    public void create_로그인_안한_사용자() throws UnAuthenticationException {
        final User guest = User.GUEST_USER;

        qnaService.create(guest, DEFAULT_QUESTION);
    }

    @Test
    public void update() throws UnAuthenticationException {
        final Long questionId = 1L;
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        final QuestionPost questionPost = new QuestionPost("title", "contents");
        final Question mockQuestion = mock(Question.class);
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(mockQuestion));
        when(mockQuestion.update(loginUser, questionPost)).thenReturn(mockQuestion);


        final Question update = qnaService.update(loginUser, questionId, questionPost);


        verify(mockQuestion, times(1)).update(loginUser, questionPost);
        softly.assertThat(update).isEqualTo(mockQuestion);
    }

    @Test(expected = EntityNotFoundException.class)
    public void update_질문_아이디_없을때() throws UnAuthenticationException {
        final Long notExistId = 9L;
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        final QuestionPost question = new QuestionPost("title", "contents");


        qnaService.update(loginUser, notExistId, question);
    }

    @Test(expected = UnAuthenticationException.class)
    public void update_로그인_안한_사용자() throws UnAuthenticationException {
        final User guest = User.GUEST_USER;
        final Long questionId = 1L;
        final QuestionPost questionPost = new QuestionPost("title", "contents");


        qnaService.update(guest, questionId, questionPost);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_글_작성자가_다를때() throws UnAuthenticationException {
        final Long questionId = 1L;
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        final User anotherUser = new User(2L,"anotherId", "password", "another", "email");
        final QuestionPost questionPost = new QuestionPost("title", "contents");
        final Question findedQuestion = new Question("title1", "contents1");
        findedQuestion.writeBy(anotherUser);
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(findedQuestion));


        qnaService.update(loginUser, questionId, questionPost);
    }

    @Test
    public void deleteQuestion_답변_없을때() throws UnAuthenticationException {
        final Long questionId = 1L;
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        final Question question = new Question(1L, "title", "contents");
        question.writeBy(loginUser);
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(question));


        qnaService.deleteQuestion(loginUser, questionId);


        verify(deleteHistoryService, times(1)).saveAll(anyList());
    }

    @Test
    public void deleteQuestion_동일_작성자_답변만_있을때() throws UnAuthenticationException {
        final Long questionId = 1L;
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        final Question question = new Question(1L, "title", "contents");
        question.writeBy(loginUser);
        final List<Answer> answers = createSameWriterAnswers(loginUser);
        question.addAllAnswer(answers);
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(loginUser, questionId);

        verify(deleteHistoryService, times(1)).saveAll(anyList());
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteQuestion_동일_작성자_다른_답변_있을때() throws UnAuthenticationException {
        final Long questionId = 1L;
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        final Question question = new Question(1L, "title", "contents");
        question.writeBy(loginUser);
        final List<Answer> answers = createSameWriterAnswers(loginUser);
        question.addAllAnswer(answers);
        final User anotherUser = new User(2L,"userId", "pass", "name", "email");
        Answer anotherUserAnswer = new Answer((long) (answers.size() + 1), anotherUser, question, "contents!!");
        question.addAnswer(anotherUserAnswer);
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(question));


        qnaService.deleteQuestion(loginUser, questionId);


        verify(deleteHistoryService, times(1)).saveAll(anyList());
    }

    private List<Answer> createSameWriterAnswers(User writer) {
        List<Answer> answers = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            Answer answer = new Answer(i, writer, null, "contents!!");
            answers.add(answer);
        }

        return answers;
    }

    @Test(expected = UnAuthenticationException.class)
    public void deleteQuestion_로그인_안함() throws UnAuthenticationException {
        final User guest = User.GUEST_USER;
        final Long questionId = 1L;


        qnaService.deleteQuestion(guest, questionId);
    }

    @Test(expected = EntityNotFoundException.class)
    public void deleteQuestion_질문_없을때() throws UnAuthenticationException {
        final Long questionId = 1L;
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.empty());


        qnaService.deleteQuestion(loginUser, questionId);
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteQuestion_작성자_다름() throws UnAuthenticationException {
        final Long questionId = 1L;
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        final User anotherUser = new User(9L, "id", "pwd", "nm", "em");
        final Question findedQuestion = new Question();
        findedQuestion.writeBy(anotherUser);
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(findedQuestion));


        qnaService.deleteQuestion(loginUser, questionId);
    }

    @Test
    public void addAnswer() throws UnAuthenticationException {
        final Long questionId = 1L;
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        final Answer answer = new Answer("answer!!");
        final Question mockQuestion = mock(Question.class);
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.of(mockQuestion));


        final Answer addAnswer = qnaService.addAnswer(loginUser, questionId, answer);


        verify(mockQuestion, times(1)).addAnswer(any(Answer.class));
        softly.assertThat(addAnswer).isEqualTo(answer);
        softly.assertThat(addAnswer.isOwner(loginUser)).isTrue();
    }

    @Test(expected = UnAuthenticationException.class)
    public void addAnswer_로그인_안했을때() throws UnAuthenticationException {
        final Long questionId = 1L;
        final User guest = User.GUEST_USER;
        final Answer answer = new Answer("answer!!");


        qnaService.addAnswer(guest, questionId, answer);
    }

    @Test(expected = EntityNotFoundException.class)
    public void addAnswer_질문_없을때() throws UnAuthenticationException {
        final Long questionId = Long.MAX_VALUE;
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        final Answer answer = new Answer("answer!!");
        when(questionRepository.findByIdAndDeleted(questionId, false)).thenReturn(Optional.empty());


        qnaService.addAnswer(loginUser, questionId, answer);
    }

    @Test
    public void deleteAnswer() throws UnAuthenticationException {
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        final Long answerId = 1L;
        final Long questionId = 1L;
        final Answer mockAnswer = mock(Answer.class);
        when(answerRepository.findAnswerByIdAndDeleted(answerId, false)).thenReturn(Optional.of(mockAnswer));

        qnaService.deleteAnswer(loginUser, questionId, answerId);

        verify(mockAnswer, times(1)).delete(loginUser, questionId);
        verify(deleteHistoryService, times(1)).saveAll(anyList());
    }

    @Test(expected = UnAuthenticationException.class)
    public void deleteAnswer_로그인_안했을때() throws UnAuthenticationException {
        final User guest = User.GUEST_USER;
        final Long questionId = 1L;
        final Long answerId = 1L;

        qnaService.deleteAnswer(guest, questionId, answerId);
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteAnswer_작성자_다를때() throws UnAuthenticationException {
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        final User anotherUser = new User(9L, "id", "pwd", "nm", "em");
        final Long questionId = 1L;
        final Long answerId = 1L;
        final Answer findedAnswer = new Answer(loginUser, "contents!!");
        when(answerRepository.findAnswerByIdAndDeleted(answerId, false)).thenReturn(Optional.of(findedAnswer));

        qnaService.deleteAnswer(anotherUser, questionId, answerId);
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteAnswer_questionId_다를때() throws UnAuthenticationException {
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        final Question question = new Question(1L, "title", "contents");
        final Long answerId = 1L;
        final Answer findedAnswer = new Answer(loginUser, "contents!!");
        findedAnswer.toQuestion(question);
        when(answerRepository.findAnswerByIdAndDeleted(answerId, false)).thenReturn(Optional.of(findedAnswer));

        qnaService.deleteAnswer(loginUser, question.getId() + 1, answerId);
    }

    @Test(expected = EntityNotFoundException.class)
    public void deleteAnswer_답변_없을때() throws UnAuthenticationException {
        final User loginUser = new User(1L,"userId", "pass", "name", "email");
        final Long questionId = 1L;
        final Long answerId = Long.MAX_VALUE;
        when(answerRepository.findAnswerByIdAndDeleted(answerId, false)).thenReturn(Optional.empty());

        qnaService.deleteAnswer(loginUser, questionId, answerId);
    }

}