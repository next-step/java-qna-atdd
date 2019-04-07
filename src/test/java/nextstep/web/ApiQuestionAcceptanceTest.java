package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void question_read_no_login() {
        Question question = defaultQuestion();

        // when
        ResponseEntity<Question> response = getQuestionResourceWithoutLogin(question.generateRestUrl());

        // then
        Question dbQuestion = response.getBody();
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        softly.assertThat(dbQuestion).isEqualTo(question);
    }

    @Test
    public void question_create_no_login() {
        // given
        String title = "Hello";
        String contents = "World";
        Question question = new Question(title, contents);

        // when
        ResponseEntity<Question> response = createResourceWithoutLogin("/api/questions", question, Question.class);

        // then
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void question_create_login() {
        // given
        User loginUser = defaultUser();
        String title = "Hello";
        String contents = "World";
        Question question = new Question(title, contents);

        // when
        ResponseEntity<Question> response = createQuestionResource(loginUser, question);

        // then
        String location = response.getHeaders().getLocation().getPath();
        Question dbQuestion = getQuestionResourceWithoutLogin(location).getBody();

        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED);
        softly.assertThat(dbQuestion.getTitle()).isEqualTo(title);
        softly.assertThat(dbQuestion.getContents()).isEqualTo(contents);
    }

    @Test
    public void question_update_no_login() {
        // given
        Question question = defaultQuestion();

        // when
        String updatedTitle = "Hello";
        String updatedContents = "World";
        question.setTitle(updatedTitle);
        question.setContents(updatedContents);

        ResponseEntity<Question> response = updateResourceWithoutLogin(question.generateRestUrl(), question, Question.class);

        // then
        Question dbQuestion = questionRepository.findById(question.getId()).get();
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
        softly.assertThat(dbQuestion.getTitle()).isNotEqualTo(updatedTitle);
        softly.assertThat(dbQuestion.getContents()).isNotEqualTo(updatedContents);

        log.debug("response body : {}", response.getBody());
    }

    @Test
    public void question_update_login() {
        // given
        User loginUser = defaultUser();
        Question question = defaultQuestion();

        // when
        String updatedTitle = "Hello";
        String updatedContents = "World";
        question.setTitle(updatedTitle);
        question.setContents(updatedContents);

        ResponseEntity<Question> response = updateQuestionResource(loginUser, String.format("/api/questions/%d", question.getId()), question);

        // then
        Question dbQuestion = questionRepository.findById(question.getId()).get();
        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        softly.assertThat(dbQuestion.getTitle()).isEqualTo(updatedTitle);
        softly.assertThat(dbQuestion.getContents()).isEqualTo(updatedContents);

        log.debug("response body : {}", response.getBody());
    }

    @Test
    public void question_delete_no_login() {
        // given
        Question question = defaultQuestion();

        // when
        ResponseEntity<String> response = deleteResourceWithoutLogin(question.generateRestUrl(), String.class);

        // then
        Question dbQuestion = getQuestionResourceWithoutLogin(question.generateRestUrl()).getBody();

        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
        softly.assertThat(dbQuestion.isDeleted()).isFalse();

        log.debug("response body : {}", response.getBody());
    }

    @Test
    public void question_delete_login() {
        // given
        User loginUser = defaultUser();
        Question question = defaultQuestion();

        // when
        ResponseEntity<String> response = deleteQuestionResource(loginUser, question);

        // then
        Question dbQuestion = getQuestionResourceWithoutLogin(question.generateRestUrl()).getBody();

        softly.assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.NO_CONTENT);
        softly.assertThat(dbQuestion.isDeleted()).isTrue();

        log.debug("response body : {}", response.getBody());

        // tearDown
        restoreDeletedQuestion(dbQuestion);
    }

    private ResponseEntity<Question> getQuestionResourceWithoutLogin(String location) {
        return getResourceWithoutLogin(location, Question.class);
    }

    private ResponseEntity<Question> createQuestionResource(User loginUser, Question question) {
        return createResource(loginUser, "/api/questions", question, Question.class);
    }

    private ResponseEntity<Question> updateQuestionResource(User loginUser, String location, Question updatedQuestion) {
        return updateResource(loginUser, location, updatedQuestion, Question.class);
    }

    private ResponseEntity<String> deleteQuestionResource(User loginUser, Question question) {
        return deleteResource(loginUser, String.format("/api/questions/%d", question.getId()), String.class);
    }

    private void restoreDeletedQuestion(Question deletedQuestion) {
        Question question = new Question(deletedQuestion.getId(), deletedQuestion.getTitle(),
                deletedQuestion.getContents(), deletedQuestion.getWriter(), false);

        questionRepository.save(question);
    }
}
