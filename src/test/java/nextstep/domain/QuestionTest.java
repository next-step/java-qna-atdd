package nextstep.domain;

import nextstep.UnAuthenticationException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionTest {
    public static final Question QUESTION_1 = new Question("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?"
            , "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다. Play 프레임워크는 정말 한 순간 잠시 눈에 뜨이다가 사라져 버렸다. RoR과 Play 기반으로 개발을 해보면 정말 생산성이 높으며, 웹 프로그래밍이 재미있기까지 하다. Spring MVC + JPA(Hibernate) 기반으로 진행하면 설정할 부분도 많고, 기본으로 지원하지 않는 기능도 많아 RoR과 Play에서 기본적으로 지원하는 기능을 서비스하려면 추가적인 개발이 필요하다."
        ,UserTest.JAVAJIGI
    );

    public static final Question QUESTION_2 = new Question("runtime 에 reflect 발동 주체 객체가 뭔지 알 방법이 있을까요?"
            , "설계를 희한하게 하는 바람에 꼬인 문제같긴 합니다만. 여쭙습니다. 상황은 mybatis select 실행될 시에 return object 의 getter 가 호출되면서인데요. getter 안에 다른 property 에 의존중인 코드가 삽입되어 있어서, 만약 다른 mybatis select 구문에 해당 property 가 없다면 exception 이 발생하게 됩니다."
            ,UserTest.SANJIGI
    );

    public Question newQuestion(User user, long id) {
        return new Question("타이틀", "본문", user);
    }

    @Test
    public void update_eqaul_user() throws UnAuthenticationException {
        User user  = UserTest.JAVAJIGI;
        Question origin = QUESTION_1;
        Question target = newQuestion(user, 1);

        origin.update(origin, target);
        assertThat(origin.getContents()).isEqualTo(target.getContents());
        assertThat(origin.getTitle()).isEqualTo(target.getTitle());
        assertThat(origin.getWriter()).isEqualTo(target.getWriter());
    }

    @Test(expected = UnAuthenticationException.class)
    public void update_not_equal_user() throws UnAuthenticationException {
        User user  = UserTest.SANJIGI;
        Question origin = QUESTION_1;
        Question target = newQuestion(user, 1);

        origin.update(origin, target);
        assertThat(origin.getContents()).isEqualTo(target.getContents());
        assertThat(origin.getTitle()).isEqualTo(target.getTitle());
        assertThat(origin.getWriter()).isEqualTo(target.getWriter());
    }




}