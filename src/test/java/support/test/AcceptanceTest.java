package support.test;

import nextstep.domain.User;
import nextstep.domain.UserRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest extends BaseTest {
    private static final String DEFAULT_LOGIN_USER = "javajigi";

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private UserRepository userRepository;

    public TestRestTemplate template() {
        return template;
    }

    public TestRestTemplate basicAuthTemplate() {
        return basicAuthTemplate(defaultUser());
    }

    public TestRestTemplate basicAuthTemplate(User loginUser) {
        return template.withBasicAuth(loginUser.getUserId(), loginUser.getPassword());
    }

    protected User defaultUser() {
        return findByUserId(DEFAULT_LOGIN_USER);
    }

    protected User findByUserId(String userId) {
        return userRepository.findByUserId(userId).get();
    }

    protected String createResource(User user) {
        ResponseEntity<String> response = template().postForEntity("/api/users", user, String.class);
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED);
        return response.getHeaders().getLocation().getPath();
    }

    protected ResponseEntity<User> getUserResourceResponseEntity(String location, User loginUser) {
        return getResourceResponseEntity(location, User.class, loginUser);
    }

    protected <T> ResponseEntity<T> getResourceResponseEntity(String location, Class<T> type, User loginUser) {
        return basicAuthTemplate(loginUser).getForEntity(location, type);
    }

    protected ResponseEntity<User> putUserResourceResponseEntity(String location, HttpEntity httpEntity, User loginUser) {
        return putResourceResponseEntity(location, httpEntity, User.class, loginUser);
    }

    protected <T> ResponseEntity<T> putResourceResponseEntity(String location, HttpEntity httpEntity, Class<T> type, User loginUser) {
        return basicAuthTemplate(loginUser).exchange(location, HttpMethod.PUT, httpEntity, type);
    }
}
