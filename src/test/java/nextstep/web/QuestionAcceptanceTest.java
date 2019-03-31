package nextstep.web;

import nextstep.domain.User;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class QuestionAcceptanceTest extends AcceptanceTest {

    @Test
    public void question_form_no_login() {
        // when
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void question_form_login() {
        // given
        User loginUser = defaultUser();

        // when
        ResponseEntity<String> response = basicAuthTemplate(loginUser).getForEntity("/questions/form", String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    }
}
