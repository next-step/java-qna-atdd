package nextstep.web;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

public class LoginAcceptanceTest extends AcceptanceTest {

  @Test
  public void login() {

    HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
        .addParameter("userId", "javajigi")
        .addParameter("password", "test")
        .build();

    ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

    softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
  }
}
