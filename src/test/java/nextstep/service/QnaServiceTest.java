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


import java.util.Optional;

import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    private final Long questionId = 1L;
    private final Long answerId = 1L;

    User user1;
    User user2;
    Question question;
    Answer answer;

    @Before
    public void setUp() {
        user1 = UserTest.JAVAJIGI;
        user2 = UserTest.SANJIGI;
        question = new Question(questionId, "질문 타이틀", "질문 내용~~~", user1);
        answer = new Answer(answerId, user1, "첫 답변");
    }

    @Test
    public void create_question() {
        when(questionRepository.save(question)).thenReturn(question);
        Question savedQuestion = qnaService.create(user1, question);
        softly.assertThat(savedQuestion).isEqualTo(question);
    }

    @Test
    public void update_question_success() {
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        Question updatedQuestion = new Question(questionId, "질문 타이틀 수정용", "질문 내용 수정용~~~", user1);
        Question target = qnaService.update(user1, questionId, updatedQuestion);
        softly.assertThat(target).isEqualTo(updatedQuestion);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_question_failed_when_user_not_owner() {
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        qnaService.update(user2, questionId, question);
    }

    @Test
    public void delete_question_success() throws CannotDeleteException {
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        qnaService.deleteQuestion(user1, questionId);
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_question_failed_when_user_not_owner() throws CannotDeleteException {
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        qnaService.deleteQuestion(user2, questionId);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_Answer_failed_when_user_not_owner() throws CannotDeleteException {
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(answerRepository.save(answer)).thenReturn(answer);

        Question question = qnaService.findById(questionId);
        question.addAnswer(answer);

        qnaService.addAnswer(user2, questionId, answer);
        qnaService.deleteQuestion(user2, answerId);
    }

}