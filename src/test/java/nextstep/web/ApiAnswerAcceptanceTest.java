package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.AnswerRepository;
import nextstep.domain.Question;
import nextstep.domain.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    
    @Autowired
    private AnswerRepository answerRepository;

    @Test
    public void answer_read_no_login() {
        // given
        Answer answer = defaultAnswer();

        // when
        ResponseEntity<Answer> response = getAnswerResource(answer.generateUrl());

        // then
        Answer dbAnswer = response.getBody();
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        softly.assertThat(answer.getContents()).isEqualTo(dbAnswer.getContents());
    }

    @Test
    public void answer_create_no_login() {
        // given
        Question question = defaultQuestion();
        String contents = "Hello World";

        // when
        ResponseEntity<String> response = createAnswerResourceWithoutLogin(contents, question.getId());

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void answer_create_login() {
        // given
        User loginUser = defaultUser();
        Question question = defaultQuestion();
        String contents = "Hello World";

        // when
        ResponseEntity<String> response = createAnswerResource(loginUser, contents, question.getId());
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED);

        // then
        Answer dbAnswer = getAnswerResource(response.getHeaders().getLocation().getPath()).getBody();

        softly.assertThat(dbAnswer.getWriter()).isEqualTo(loginUser);
        softly.assertThat(dbAnswer.getContents()).isEqualTo(contents);
        softly.assertThat(dbAnswer.getQuestion()).isEqualTo(question);
    }

    @Test
    public void answer_delete_no_login() {
        // given
        Answer answer = defaultAnswer();

        // when
        ResponseEntity<String> response = deleteResourceWithoutLogin(answer.generateUrl(), String.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
        softly.assertThat(defaultAnswer().isDeleted()).isFalse();
    }

    @Test
    public void answer_delete_login_작성자() {
        // given
        User loginUser = defaultUser();
        Answer answer = defaultAnswer();

        // when
        ResponseEntity<String> response = deleteAnswerResource(loginUser, answer);

        // then
        Answer deletedAnswer = answerRepository.findById(answer.getId()).get();
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.NO_CONTENT);
        softly.assertThat(deletedAnswer.isDeleted()).isTrue();
    }

    @Test
    public void answer_update_no_login() {
        // given
        Answer answer = defaultAnswer();

        // when
        String modifiedContents = "Hello World";
        answer.setContents(modifiedContents);

        ResponseEntity<Answer> response = updateResourceWithoutLogin(answer.generateUrl(), modifiedContents, Answer.class);

        // then
        Answer dbAnswer = answerRepository.findById(answer.getId()).get();
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
        softly.assertThat(dbAnswer.getContents()).isNotEqualTo(modifiedContents);
    }

    @Test
    public void answer_update_login() {
        // given
        User loginUser = defaultUser();
        Answer answer = defaultAnswer();

        // when
        String modifiedContents = "Hello World";
        answer.setContents(modifiedContents);

        ResponseEntity<Answer> response = updateAnswerResource(loginUser, answer.generateUrl(), answer);

        // then
        Answer dbAnswer = answerRepository.findById(answer.getId()).get();
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        softly.assertThat(dbAnswer.getContents()).isEqualTo(modifiedContents);
    }

    private Answer otherAnswer() {
        return answerRepository.findById(2L).get();
    }

    private ResponseEntity<Answer> getAnswerResource(String location) {
        ResponseEntity<Answer> response = getResourceWithoutLogin(location, Answer.class);
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        return response;
    }

    private ResponseEntity<String> createAnswerResourceWithoutLogin(String contents, long questionId) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("contents", contents)
                .addParameter("questionId", questionId)
                .build();

        return template().postForEntity(String.format("/api/questions/%d/answers", questionId), request, String.class);
    }

    private ResponseEntity<String> createAnswerResource(User loginUser, String contents, long questionId) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("contents", contents)
                .addParameter("questionId", questionId)
                .build();

        return basicAuthTemplate(loginUser).postForEntity(String.format("/api/questions/%d/answers", questionId), request, String.class);
    }

    private ResponseEntity<String> deleteAnswerResource(User loginUser, Answer answer) {
        return deleteResource(loginUser, answer.generateUrl(), String.class);
    }

    private ResponseEntity<Answer> updateAnswerResource(User loginUser, String location, Answer updatedAnswer) {
        return updateResource(loginUser, location, updatedAnswer, Answer.class);
    }
}
