package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.QuestionNotFoundException;
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

    User user;
    User diffUser;
    Question question;

    @Before
    public void setup() {
        user = new User("javajigi", "password", "name", "javajigi@slipp.net");
        question = new Question("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?",
                "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다. Play 프레임워크는 정말 한 순간 잠시 눈에 뜨이다가 사라져 버렸다. " +
                        "RoR과 Play 기반으로 개발을 해보면 정말 생산성이 높으며, 웹 프로그래밍이 재미있기까지 하다. Spring MVC + JPA(Hibernate) " +
                        "기반으로 진행하면 설정할 부분도 많고, 기본으로 지원하지 않는 기능도 많아 RoR과 Play에서 기본적으로 지원하는 기능을 서비스하려면 " +
                        "추가적인 개발이 필요하다.");
        diffUser = new User(3L, "testuser", "password", "name", "test@slipp.net");
        question.writeBy(user);
    }

    @Test
    public void update_question() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        Question newQuestion = qnaService.update(user, 1L, new Question("wwowowow", "wowowow"));
        softly.assertThat(newQuestion).isEqualTo(question);
    }

    @Test (expected = UnAuthorizedException.class)
    public void update_question_where_login_user_is_not_writer() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.update(diffUser, 1L, new Question("test update", "test update"));
    }

    @Test
    public void delete_question() throws CannotDeleteException {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(user, 1L);
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test (expected = QuestionNotFoundException.class)
    public void delete_not_existing_question() throws CannotDeleteException {
        qnaService.deleteQuestion(user, 3L);
    }

    @Test (expected = CannotDeleteException.class)
    public void 질문_작성자와_로그인한_사용자가_다른_경우_테스트() throws CannotDeleteException {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(diffUser, 1L);
    }

    @Test
    public void 질문의_답변_작성자가_로그인한_사용자와_같은_경우() throws CannotDeleteException {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(answerRepository.save(new Answer())).thenReturn(new Answer(user, "answer"));

        Question questionWithAnswer = qnaService.findById(1L).orElseThrow(QuestionNotFoundException::new);
        questionWithAnswer.addAnswer(new Answer(user, "answer"));

        qnaService.addAnswer(user, 1L, "answer");
        Question deleted = qnaService.deleteQuestion(user, 1L);

        softly.assertThat(deleted.isDeleted()).isTrue();
    }

    @Test (expected = CannotDeleteException.class)
    public void 질문의_답변_작성자가_로그인한_사용자와_다른_경우() throws CannotDeleteException {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(answerRepository.save(new Answer())).thenReturn(new Answer(diffUser, "answer"));

        Question questionWithAnswer = qnaService.findById(1L).orElseThrow(QuestionNotFoundException::new);
        questionWithAnswer.addAnswer(new Answer(diffUser, "answer"));

        qnaService.addAnswer(user, 1L, "answer");
        qnaService.deleteQuestion(user, 1L);
    }
}