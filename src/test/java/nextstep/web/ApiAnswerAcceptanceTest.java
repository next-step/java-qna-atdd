package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

    @Test
    public void addAnwer_login() {
        String contents = "이것은 답변입니다.";
        String location = createResourceBasicAuth("/api/questions/1/answers/", contents);

        Answer answer = getResource(location,Answer.class,defaultUser());
        softly.assertThat(answer.getContents()).contains("답변");
    }

    @Test
    public void show_answer(){
        Answer answer = getResource("/api/questions/1/answers/2", Answer.class, defaultUser());
        softly.assertThat(answer.getContents()).contains("언더스코어");
    }

    @Test
    public void delete_answer(){
        String contents = "api콜 삭제 요청시 컨텐츠 값은 의미가 없지 않을까.";
        String location = createResourceBasicAuth("/api/questions/1/answers/", contents);
        ResponseEntity<Answer> responseEntity = getResourceDelete(location, defaultUser());
        softly.assertThat(responseEntity.getBody().isDeleted()).isTrue();
    }

}
