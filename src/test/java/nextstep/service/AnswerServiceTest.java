package nextstep.service;

import nextstep.UnAuthorizedException;
import nextstep.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AnswerServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    private static final String DEFAULT_LOGIN_USER = "javajigi";
    private final Question question = new Question("question title", "nextstep contents");
    private final User user = new User(0,"javajigi", "test", "자바지기", "javajigi@slipp.net");
    private final User notLoginUser = new User(1,"sanjigi", "test", "산지기", "sanjigi@slipp.net");
    private final Answer answer = new Answer(user, "first answer");

    @Test
    public void add_answer_success() {
        when(questionRepository.findById(user.getId())).thenReturn(Optional.of(question));

        Answer answer = qnaService.addAnswer(user, question.getId(), "answer test");
        softly.assertThat(answer.getContents()).isEqualTo("answer test");
    }

    @Test(expected = NoSuchElementException.class)
    public void create_question_no_login() {
        when(userRepository.findByUserId(DEFAULT_LOGIN_USER)).thenReturn(Optional.empty());
        User user = userRepository.findByUserId(DEFAULT_LOGIN_USER).get();

        when(answerRepository.findById(user.getId())).thenReturn(Optional.of(answer));

        qnaService.addAnswer(user, question.getId(), "answer test");
    }

    @Test
    public void delete_answer_success() {
        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));

        qnaService.deleteAnswer(user, answer.getId());
        softly.assertThat(answer.isDeleted()).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_answer_not_writer() {
        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));

        qnaService.deleteAnswer(notLoginUser, answer.getId());
    }
}
