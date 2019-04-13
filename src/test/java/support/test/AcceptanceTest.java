package support.test;

import nextstep.domain.User;
import nextstep.domain.UserRepository;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static nextstep.domain.UserTest.ANOTHER_USER;
import static nextstep.domain.UserTest.SELF_USER;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest extends BaseTest {

    @Autowired
    protected TestRestTemplate template;

    @Autowired
    protected UserRepository userRepository;

    public TestRestTemplate template() {
        return template;
    }

    public TestRestTemplate basicAuthTemplate() {
        return basicAuthTemplate(selfUser());
    }

    public TestRestTemplate basicAuthTemplate(User loginUser) {
        return template.withBasicAuth(loginUser.getUserId(), loginUser.getPassword());
    }

    protected User selfUser() {
        return findByUserId(SELF_USER.getUserId());
    }

    protected User anotherUser() {
        return findByUserId(ANOTHER_USER.getUserId());
    }

    protected User findByUserId(String userId) {
        return userRepository.findByUserId(userId).get();
    }
}
