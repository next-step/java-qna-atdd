package nextstep.web;

import nextstep.domain.User;
import nextstep.domain.UserRepository;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import java.util.Optional;

import static org.mockito.Mockito.when;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Mock
    private UserRepository userRepository;

    @Test
    public void login_form_redirect() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/login", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains("/login");
    }

    @Test
    public void login() throws Exception {
        User user = new User(defaultUser().getUserId(), defaultUser().getPassword(), defaultUser().getName(), defaultUser().getEmail());
        when(userRepository.findByUserId(user.getUserId())).thenReturn(Optional.of(user));

        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", defaultUser().getUserId())
                .addParameter("password", defaultUser().getPassword())
                .build();

        ResponseEntity<String> response = template()
                .postForEntity("/login", request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void without_id_login() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("password", defaultUser().getPassword())
                .build();

        ResponseEntity<String> response = template()
                .postForEntity("/login", request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains("alert-danger");
    }

    @Test
    public void without_password_login() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", defaultUser().getUserId())
                .build();

        ResponseEntity<String> response = template()
                .postForEntity("/login", request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains("alert-danger");
    }

    @Test
    public void failed_login() throws Exception{
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", defaultUser().getUserId())
                .addParameter("password", defaultUser().getUserId())
                .build();

        ResponseEntity<String> response = template()
                .postForEntity("/login", request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains("alert-danger");
    }
}
