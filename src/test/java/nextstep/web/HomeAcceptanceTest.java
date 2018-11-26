package nextstep.web;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import support.test.AcceptanceTest;

public class HomeAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(HomeAcceptanceTest.class);

    @Test
    public void list() {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains("국내에서 Ruby on Rails와 Play");
    }
}
