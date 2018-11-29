package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    public static final Question QUESTION_1 = new Question(1L,"국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?"
            , "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다. Play 프레임워크는 정말 한 순간 잠시 눈에 뜨이다가 사라져 버렸다. RoR과 Play 기반으로 개발을 해보면 정말 생산성이 높으며, 웹 프로그래밍이 재미있기까지 하다. Spring MVC + JPA(Hibernate) 기반으로 진행하면 설정할 부분도 많고, 기본으로 지원하지 않는 기능도 많아 RoR과 Play에서 기본적으로 지원하는 기능을 서비스하려면 추가적인 개발이 필요하다."
        ,UserTest.JAVAJIGI
    );

    public static final Question QUESTION_2 = new Question(2L,"runtime 에 reflect 발동 주체 객체가 뭔지 알 방법이 있을까요?"
            , "설계를 희한하게 하는 바람에 꼬인 문제같긴 합니다만. 여쭙습니다. 상황은 mybatis select 실행될 시에 return object 의 getter 가 호출되면서인데요. getter 안에 다른 property 에 의존중인 코드가 삽입되어 있어서, 만약 다른 mybatis select 구문에 해당 property 가 없다면 exception 이 발생하게 됩니다."
            ,UserTest.SANJIGI
    );

    public static Question newQuestion(User user) {
        return newQuestion(user, "타이틀틀틀틀", "본문입니다다다다");
    }

    public static Question newQuestion(User user, String title, String contents) {
        return new Question(title, contents, user);
    }

    @Test
    public void update_eqaul_user() throws CannotUpdateException {
        User user  = UserTest.JAVAJIGI;
        Question origin = QUESTION_1;
        Question target = new Question(origin.getId(), "제목수정", "본문수정", user);

        origin.update(target, user);
        softly.assertThat(origin.getContents()).isEqualTo(target.getContents());
        softly.assertThat((origin.getTitle())).isEqualTo(target.getTitle());
        softly.assertThat((origin.getWriter())).isEqualTo(target.getWriter());
    }

    @Test(expected = CannotUpdateException.class)
    public void update_not_equal_user() throws CannotUpdateException {
        User user  = UserTest.SANJIGI;
        Question origin = QUESTION_1;
        Question target = new Question(origin.getId(), "제목수정", "본문수정", UserTest.SANJIGI);

        origin.update(target, user);
        softly.assertThat(origin.getContents()).isEqualTo(target.getContents());
        softly.assertThat((origin.getTitle())).isEqualTo(target.getTitle());
        softly.assertThat((origin.getWriter())).isEqualTo(target.getWriter());
    }
    
    @Test
    public void delete_succes_has_not_answer() throws CannotDeleteException {
        User loginUser = UserTest.JAVAJIGI;
        Question question = new Question("신규타이틀", "내용내용내용", loginUser);
        question.delete(loginUser);
    }
    
    @Test
    public void delete_succes_has_answer() throws CannotDeleteException {
        User loginUser = UserTest.JAVAJIGI;
        Question question = new Question("신규타이틀", "내용내용내용", loginUser);
        Answer answer = new Answer(loginUser, "응답응답응답");
        question.addAnswer(answer);
        question.delete(loginUser);
    }
    
    @Test(expected = CannotDeleteException.class)
    public void delete_not_eqaul_answer_writer() throws CannotDeleteException {
        User loginUser = UserTest.JAVAJIGI;
        Question question = new Question("신규타이틀", "내용내용내용", loginUser);
        Answer answer = new Answer(UserTest.SANJIGI, "응답응답응답");
        question.addAnswer(answer);
        question.delete(loginUser);;
    }
    
    @Test(expected = CannotDeleteException.class)
    public void delete_not_eqaul_writer() throws CannotDeleteException {
        User loginUser = UserTest.JAVAJIGI;
        Question question = new Question("신규타이틀", "내용내용내용", loginUser);
        Answer answer = new Answer(loginUser, "응답응답응답");
        question.addAnswer(answer);
        question.delete(UserTest.SANJIGI);;
    }
 

}