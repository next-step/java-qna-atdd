package support.test;

import nextstep.domain.User;
import nextstep.domain.UserRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest extends BaseTest {
    private static final String DEFAULT_LOGIN_USER = "javajigi";
    private static final String ANOTHER_LOGIN_USER = "sanjigi";

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

    protected User anotherUser() {
        return findByUserId(ANOTHER_LOGIN_USER);
    }

    protected User findByUserId(String userId) {
        return userRepository.findByUserId(userId).get();
    }

    protected URI createResource(String url, Object requestParam, Object... uriVariables) {

        final ResponseEntity<Void> response = basicAuthTemplate().postForEntity(url, requestParam, Void.class, uriVariables);

        if (response.getStatusCode() != HttpStatus.CREATED) {
            throw new IllegalStateException("리소스가 생성되지 않았습니다.");
        }

        return response.getHeaders().getLocation();
    }

    protected <T> T getResource(URI resource, Class<T> responseType, User loginUser) {
        return basicAuthTemplate(loginUser).getForObject(resource, responseType);
    }
}
