package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QuestionTest {

    public static final Question newQuestion = new Question("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?",
            "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다. Play 프레임워크는 정말 한 순간 잠시 눈에 뜨이다가 사라져 버렸다. " +
                    "RoR과 Play 기반으로 개발을 해보면 정말 생산성이 높으며, 웹 프로그래밍이 재미있기까지 하다. Spring MVC + JPA(Hibernate) " +
                    "기반으로 진행하면 설정할 부분도 많고, 기본으로 지원하지 않는 기능도 많아 RoR과 Play에서 기본적으로 지원하는 기능을 서비스하려면 " +
                    "추가적인 개발이 필요하다.");


    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "userRepository")
    private UserRepository userRepository;

    @Test
    public void 본인글_update() {
        Question question = questionRepository.findById(1L).get();
        User user = userRepository.findByUserId("javajigi").get();

        boolean result = question.update(user, new Question().setTitle("test title").setContents("test contents"));
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void 본인글아닌것_update() {
        Question question = questionRepository.findById(1L).get();
        User user = userRepository.findByUserId("sanjigi").get();

        boolean result = question.update(user, new Question().setTitle("test title").setContents("test contents"));
        assertThat(result).isEqualTo(false);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문자_로그인한사람_다른경우_delete() {
        User user = userRepository.findByUserId("javajigi").get();
        User otherUser = userRepository.findByUserId("sanjigi").get();

        newQuestion.writeBy(user);
        newQuestion.delete(otherUser);
    }

    @Test
    public void 질문_답변없는경우_delete() {
        User user = userRepository.findByUserId("javajigi").get();
        newQuestion.writeBy(user);

        List<DeleteHistory> deleteHistories = newQuestion.delete(user);
        assertThat(deleteHistories.size()).isEqualTo(1);
        assertThat(newQuestion.isDeleted()).isEqualTo(true);
    }

    @Test
    public void 질문자_답변자_일치_delete() {
        User user = userRepository.findByUserId("javajigi").get();
        newQuestion.writeBy(user);
        newQuestion.addAnswer(new Answer(user, "test answer contents"));

        List<DeleteHistory> deleteHistories = newQuestion.delete(user);
        assertThat(deleteHistories.size()).isEqualTo(2);
        assertThat(newQuestion.isDeleted()).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문자_답변자_불일치_delete() {
        User user = userRepository.findByUserId("javajigi").get();
        User otherUser = userRepository.findByUserId("sanjigi").get();

        newQuestion.writeBy(user);
        newQuestion.addAnswer(new Answer(otherUser, "test answer contents"));
        newQuestion.delete(user);
    }
}