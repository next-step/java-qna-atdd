package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import java.util.List;

public class QuestionTest extends BaseTest {
    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");
    public static final Question ORIGIN = new Question("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?",
            "Ruby on Rails(이하 RoR)는 2006년 즈음에 정말 뜨겁게 달아올랐다가 금방 가라 앉았다. Play 프레임워크는 정말 한 순간 잠시 눈에 뜨이다가 사라져 버렸다. " +
                    "RoR과 Play 기반으로 개발을 해보면 정말 생산성이 높으며, 웹 프로그래밍이 재미있기까지 하다. Spring MVC + JPA(Hibernate) " +
                    "기반으로 진행하면 설정할 부분도 많고, 기본으로 지원하지 않는 기능도 많아 RoR과 Play에서 기본적으로 지원하는 기능을 서비스하려면 " +
                    "추가적인 개발이 필요하다.");

    @Test
    public void update_question() {
        ORIGIN.writeBy(JAVAJIGI);
        Question newQuestion = new Question("test", "test contents");

        ORIGIN.update(JAVAJIGI, newQuestion);
        softly.assertThat(ORIGIN.getContents()).isEqualTo(newQuestion.getContents());
        softly.assertThat(ORIGIN.getTitle()).isEqualTo(newQuestion.getTitle());

    }

    @Test(expected = UnAuthorizedException.class)
    public void update_question_by_not_writer() {
        ORIGIN.writeBy(JAVAJIGI);
        Question newQuestion = new Question("test", "test contents");

        ORIGIN.update(SANJIGI, newQuestion);
    }

    @Test
    public void 질문_작성자와_로그인한_사용자가_같은_경우() throws CannotDeleteException {
        ORIGIN.writeBy(JAVAJIGI);
        List<DeleteHistory> histories = ORIGIN.delete(JAVAJIGI);

        softly.assertThat(ORIGIN.isDeleted()).isTrue();
        softly.assertThat(histories.size()).isEqualTo(1);
    }

    @Test(expected = CannotDeleteException.class)
    public void 질문_작성자와_로그인한_사용자가_다른_경우() throws CannotDeleteException {
        ORIGIN.writeBy(JAVAJIGI);
        ORIGIN.delete(SANJIGI);
    }

    @Test
    public void 질문과_답변_작성자가_로그인한_사용자와_같은_경우() throws CannotDeleteException {
        Answer answer = new Answer(JAVAJIGI, "Test answer");
        ORIGIN.writeBy(JAVAJIGI);
        ORIGIN.addAnswer(answer);
        List<DeleteHistory> histories = ORIGIN.delete(JAVAJIGI);
        softly.assertThat(ORIGIN.isDeleted()).isTrue();
        softly.assertThat(answer.isDeleted()).isTrue();
        softly.assertThat(histories.size()).isEqualTo(2);

    }

    @Test (expected = CannotDeleteException.class)
    public void 질문과_답변_작성자가_로그인한_사용자와_다른_경우() throws CannotDeleteException {
        Answer answer = new Answer(SANJIGI, "Test answer");
        ORIGIN.writeBy(JAVAJIGI);
        ORIGIN.addAnswer(answer);
        ORIGIN.delete(JAVAJIGI);
    }
}

