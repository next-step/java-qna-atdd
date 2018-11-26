package nextstep.web;

import nextstep.domain.UserRepository;
import nextstep.util.HtmlFormDataBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

@SuppressWarnings({"SpellCheckingInspection", "NonAsciiCharacters"})
public class LoginAcceptanceTest extends AcceptanceTest {

    @Autowired
    private UserRepository userRepository;

    @SuppressWarnings("ConstantConditions")
    @Test
    public void 로그인_성공() {

        final String userId = "ninezero90hy";
        final HttpEntity<MultiValueMap<String, Object>> request = createRequest(userId, "ninezero90hy@");
        final ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(userRepository.findByUserId(userId).isPresent()).isTrue();
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
    }

    @Test
    public void 로그인_실패() {

        final String userId = "ninezero90hy";
        final HttpEntity<MultiValueMap<String, Object>> request = createRequest(userId, "password");
        final ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @SuppressWarnings("SameParameterValue")
    private HttpEntity<MultiValueMap<String, Object>> createRequest(final String userId, final String password) {
        return HtmlFormDataBuilder.urlEncodedForm()
                .post()
                .addParameter("userId", userId)
                .addParameter("password", password)
                .addParameter("name", "나인제로")
                .addParameter("email", "ninezero90hy@gmail.com")
                .build();
    }

}
