package nextstep.web;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

public class LoginAcceptanceTest extends AcceptanceTest {
    @Test
    public void 로그인_성공시_리다이렉트한다() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
            .addParameter("userId", "javajigi")
            .addParameter("password", "test")
            .build();

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }

    @Test
    public void 로그인_실패시_실패화면을_출력한다() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
            .addParameter("userId", "javajigi")
            .addParameter("password", "wrong_password")
            .build();

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);
        softly.assertThat(response.getBody()).contains("아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요.");
    }
}
