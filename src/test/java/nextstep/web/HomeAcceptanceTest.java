package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionBody;
import nextstep.domain.QuestionRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class HomeAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(HomeAcceptanceTest.class);

    @Autowired private QuestionRepository questionRepository;

    @Test
    public void 로그인하면_질문하기_버튼이_출력된다() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains("질문하기");
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 로그인하지_않으면_질문하기_버튼이_출력되지_않는다() {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).doesNotContain("질문하기");
        log.debug("body : {}", response.getBody());
    }
}
