package nextstep.web;

import helper.HtmlFormDataBuilder;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

public class LoginAcceptanceTest extends AcceptanceTest {

    @Test
    public void login_success() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .post()
                .addParameter("userId", "javajigi")
                .addParameter("password", "test")
                .build();

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }

    @Test
    public void login_fail() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .post()
                .addParameter("userId", "javajigi")
                .addParameter("password", "test111")
                .build();

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}