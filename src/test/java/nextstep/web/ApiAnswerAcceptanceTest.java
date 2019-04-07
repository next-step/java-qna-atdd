package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;
import support.domain.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import java.util.Optional;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);


    @Test
    public void add() throws Exception {
        final User loginUser = defaultUser();
        Optional<Question> question = defaultQuestion();

        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("user", loginUser)
                .addParameter("questionId", question.orElseThrow(IllegalArgumentException::new).getId())
                .addParameter("contents", "first answer")
                .build();

        softly.assertThat(foundResource(getAnswerPath(""), request)).startsWith("/questions");
    }

    @Test
    public void delete() throws Exception {
        final User loginUser = defaultUser();

        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .addParameter("loginUser", loginUser)
                .build();

        softly.assertThat(foundResource(getAnswerPath("/%d"), request)).startsWith("/questions");
    }
}
