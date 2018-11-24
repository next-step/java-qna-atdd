package nextstep.service;

import nextstep.CannotDeleteException;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
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

    @InjectMocks
    private QnaService qnaService;

    User user;
    Question question;

    @Before
    public void setup() {
        user = new User("javajigi", "password", "name", "javajigi@slipp.net");
        question = new Question("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?",
                "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다. Play 프레임워크는 정말 한 순간 잠시 눈에 뜨이다가 사라져 버렸다. " +
                        "RoR과 Play 기반으로 개발을 해보면 정말 생산성이 높으며, 웹 프로그래밍이 재미있기까지 하다. Spring MVC + JPA(Hibernate) " +
                        "기반으로 진행하면 설정할 부분도 많고, 기본으로 지원하지 않는 기능도 많아 RoR과 Play에서 기본적으로 지원하는 기능을 서비스하려면 " +
                        "추가적인 개발이 필요하다.");
        question.writeBy(user);
    }

    @Test
    public void update_question() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        Question newQuestion = qnaService.update(user, 1L, new Question("wwowowow", "wowowow"));
        softly.assertThat(newQuestion).isEqualTo(question);
    }

    @Test
    public void delete_question() throws CannotDeleteException {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(user, 1L);
        softly.assertThat(question.isDeleted()).isTrue();
    }
}