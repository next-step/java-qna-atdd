package nextstep.web;

import nextstep.domain.Question;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import support.test.AcceptanceTest;

import java.net.URI;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private static final String CONTEXT = "/api/questions";

    @Test
    public void create() {
        final String url = CONTEXT;

        final ResponseEntity<Void> response = post(url, basicAuthTemplate());
        final URI location = response.getHeaders().getLocation();


        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.CREATED);


        final Question createdQuestion = getResource(location, Question.class, defaultUser());
        softly.assertThat(createdQuestion).isNotNull();
    }

    @Test
    public void create_로그인_안했을때() {
        final String url = CONTEXT;

        final ResponseEntity<Void> response = post(url, template());

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void show() {
        final URI resource = createResource(CONTEXT, new Question("newTitle", "newContents"));

        final ResponseEntity<Question> response = template().getForEntity(resource, Question.class);

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.OK);
    }

    @Test
    public void show_없는_질문() {
        final String url = CONTEXT + "/{questionId}";
        final Long questionId = Long.MAX_VALUE;

        final ResponseEntity<Question> response = template().getForEntity(url, Question.class, questionId);

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void update() {
        final URI resource = createResource(CONTEXT, new Question("newTitle", "newContents"));
        final Question origin = getResource(resource, Question.class, defaultUser());

        final ResponseEntity<Question> response = put(resource, basicAuthTemplate(), origin);
        final Question responseQuestion = response.getBody();

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.OK);

        final Question findedQuestion = getResource(resource, Question.class, defaultUser());

        softly.assertThat(responseQuestion.isEqualQuestion(findedQuestion));
    }

    @Test
    public void update_로그인_안했을때() {
        final URI resource = createResource(CONTEXT, new Question("newTitle", "newContents"));
        final Question origin = getResource(resource, Question.class, defaultUser());

        final ResponseEntity<Question> response = put(resource, template(), origin);

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update_작성자_다를때() {
        final URI resource = createResource(CONTEXT, new Question("newTitle", "newContents"));
        final Question origin = getResource(resource, Question.class, defaultUser());

        final ResponseEntity<Question> response = put(resource, basicAuthTemplate(anotherUser()), origin);

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_질문글_없을때() {
        final String url = CONTEXT + "/{questionId}";
        final Long questionId = Long.MAX_VALUE;
        final URI uri = UriComponentsBuilder.fromPath(url).buildAndExpand(questionId).toUri();

        final ResponseEntity<Question> response = put(uri, basicAuthTemplate(), new Question());

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void delete() {
        final URI resource = createResource(CONTEXT, new Question("newTitle", "newContents"));

        final ResponseEntity<Void> response = delete(resource, basicAuthTemplate());

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.NO_CONTENT);

        final ResponseEntity<Question> findResponse = basicAuthTemplate().getForEntity(resource, Question.class);

        softly.assertThat(findResponse.getStatusCode()).isSameAs(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void delete_로그인_안했을때() {
        final URI resource = createResource(CONTEXT, new Question("newTitle", "newContents"));

        final ResponseEntity<Void> response = delete(resource, template());

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_다른_작성자() {
        final URI resource = createResource(CONTEXT, new Question("newTitle", "newContents"));

        final ResponseEntity<Void> response = delete(resource, basicAuthTemplate(anotherUser()));

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_글_없을때() {
        final Long questionId = Long.MAX_VALUE;
        final String url = CONTEXT + "/{questionId}";
        final URI uri = UriComponentsBuilder.fromPath(url).buildAndExpand(questionId).toUri();

        final ResponseEntity<Void> response = delete(uri, basicAuthTemplate());

        softly.assertThat(response.getStatusCode()).isSameAs(HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Void> post(String url, TestRestTemplate testRestTemplate) {
        Question question = new Question("title", "contents");

        return testRestTemplate.postForEntity(url, question, Void.class);
    }

    private ResponseEntity<Question> put(URI resource, TestRestTemplate testRestTemplate, Question param) {
        final RequestEntity<Question> requestEntity = RequestEntity.put(resource).body(param);

        return testRestTemplate.exchange(requestEntity, Question.class);
    }

    private ResponseEntity<Void> delete(URI createdUri, TestRestTemplate testRestTemplate) {
        final RequestEntity<Void> request = RequestEntity.delete(createdUri).build();

        return testRestTemplate.exchange(request, Void.class);
    }
}
