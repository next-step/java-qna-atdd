package nextstep.web;


import org.junit.Test;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.domain.HtmlFormDataBuilder;
import support.test.AcceptanceTest;


public class LoginAcceptanceTest extends AcceptanceTest {

    private HtmlFormDataBuilder htmlFormDataBuilder;

    @Test
    public void login() throws Exception {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        htmlFormDataBuilder.addParameter("userId", "javajigi");
        htmlFormDataBuilder.addParameter("password", "test");
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }

}
