package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.helper.HtmlFormDataBuilder;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

public class AnswerAcceptanceTest extends AcceptanceTest {

    @Test
    public void create() {
        User loginUser = defaultUser();
        Question question = defaultQuestion();
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParam("contents", "답변입니다")
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions/"+question.getId()+"/answers", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath().startsWith("/questions/"+question.getId()));
    }

    @Test
    public void delete() {
        User loginUser = defaultUser();
        Question question = defaultQuestion();
        Answer answer = defaultAnswer();
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParam("_method","delete")
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions/"+question.getId()+"/answers/"+ answer.getId(), request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath().startsWith("/questions/"+question.getId()));
    }
}
