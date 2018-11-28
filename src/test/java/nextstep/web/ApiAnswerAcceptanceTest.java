package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import support.test.AcceptanceTest;

import java.net.URI;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    private static final String QUESTION_API = "/api/questions";
    private static final String ANSWER_API_CONTEXT = QUESTION_API + "/{questionId}/answers";

    @Test
    public void add() {
        final URI questionResource = createResource(QUESTION_API, new Question("newTitle", "newContents"));
        final Question origin = getResource(questionResource, Question.class, defaultUser());
        final Answer answer = new Answer(defaultUser(), "comment!!!");

        final ResponseEntity<Answer> response = post(answer, basicAuthTemplate(), origin.getId());

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.OK);


        final Question addedQuestion = getResource(questionResource, Question.class, defaultUser());

        softly.assertThat(addedQuestion.containsAnswer(response.getBody())).isTrue();

        final Answer addedAnswer = addedQuestion.getAnswers().get(0);

        softly.assertThat(addedAnswer.equals(response.getBody())).isTrue();
    }

    @Test
    public void add_로그인_안했을때() {
        final URI questionResource = createResource(QUESTION_API, new Question("newTitle", "newContents"));
        final Question origin = getResource(questionResource, Question.class, defaultUser());
        final Answer answer = new Answer(defaultUser(), "comment!!!");

        final ResponseEntity<Answer> response = post(answer, template(), origin.getId());

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void add_질문_없을때() {
        final long questionId = Long.MAX_VALUE;

        final ResponseEntity<Answer> response = post(new Answer(), basicAuthTemplate(anotherUser()), questionId);

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void delete() {
        final URI questionResource = createResource(QUESTION_API, new Question("newTitle", "newContents"));
        final Question origin = getResource(questionResource, Question.class, defaultUser());
        final Answer createdAnswer = post(new Answer(defaultUser(), "comment!!!"), basicAuthTemplate(), origin.getId()).getBody();

        final ResponseEntity<Void> response = requestDelete(basicAuthTemplate(), origin.getId(), createdAnswer.getId());

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.NO_CONTENT);
    }

    @Test
    public void delete_로그인_안했을때() {
        final URI questionResource = createResource(QUESTION_API, new Question("newTitle", "newContents"));
        final Question origin = getResource(questionResource, Question.class, defaultUser());
        final Answer createdAnswer = post(new Answer(defaultUser(), "comment!!!"), basicAuthTemplate(), origin.getId()).getBody();

        final ResponseEntity<Void> response = requestDelete(template(), origin.getId(), createdAnswer.getId());

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_questionId_다를때() {
        final URI questionResource = createResource(QUESTION_API, new Question("newTitle", "newContents"));
        final Question origin = getResource(questionResource, Question.class, defaultUser());
        final Answer createdAnswer = post(new Answer(defaultUser(), "comment!!!"), basicAuthTemplate(), origin.getId()).getBody();

        final ResponseEntity<Void> response = requestDelete(basicAuthTemplate(), origin.getId() + 1, createdAnswer.getId());

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_작성자_다를때() {
        final URI questionResource = createResource(QUESTION_API, new Question("newTitle", "newContents"));
        final Question origin = getResource(questionResource, Question.class, defaultUser());
        final Answer createdAnswer = post(new Answer(defaultUser(), "comment!!!"), basicAuthTemplate(anotherUser()), origin.getId()).getBody();

        final ResponseEntity<Void> response = requestDelete(basicAuthTemplate(), origin.getId(), createdAnswer.getId());

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_answer_없을때() {
        final URI questionResource = createResource(QUESTION_API, new Question("newTitle", "newContents"));
        final Question origin = getResource(questionResource, Question.class, defaultUser());

        final ResponseEntity<Void> response = requestDelete(basicAuthTemplate(), origin.getId(), Long.MAX_VALUE);

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Answer> post(Answer answer, TestRestTemplate testRestTemplate, Long questionId) {
        return testRestTemplate.postForEntity(ANSWER_API_CONTEXT, answer, Answer.class, questionId);
    }

    private ResponseEntity<Void> requestDelete(TestRestTemplate testRestTemplate, Long questionId, Long answerId) {
        URI uri = UriComponentsBuilder.fromPath(ANSWER_API_CONTEXT + "/{answerId}")
                .buildAndExpand(questionId, answerId).toUri();

        final RequestEntity<Void> request = RequestEntity.delete(uri).build();

        return testRestTemplate.exchange(request, Void.class);
    }


}
