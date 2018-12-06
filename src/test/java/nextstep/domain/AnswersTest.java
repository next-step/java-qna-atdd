package nextstep.domain;

import org.junit.Test;
import support.test.BaseTest;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswersTest extends BaseTest {

    @Test
    public void 전부삭제테스트() {
        Answer answer1= new Answer(basicUser, "aaa");
        Answer answer2= new Answer(basicUser, "bbb");

        Answers answers = new Answers();
        answers.add(answer1);
        answers.add(answer2);

        answers.deleteAll(basicUser);

        assertThat(answer1.isDeleted()).isTrue();
        assertThat(answer2.isDeleted()).isTrue();
    }
}
