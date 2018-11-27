package nextstep.web;

import static nextstep.domain.UserTest.newUser;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import nextstep.domain.Question;
import nextstep.domain.User;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Test
    public void create_no_login() {
        User newUser = newUser("testuser1");
        Question question = new Question("제목입니다.", "내용입니다.", newUser);
        
        ResponseEntity<Void> response = template().postForEntity("/api/questions", question, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", response.getBody());
    }
    
    @Test
    public void create_login() {
        User newUser = defaultUser();
        Question question = new Question("제목입니다.", "내용입니다.", newUser);
        String location = createResource(basicAuthTemplate(), "/api/questions", question);
        softly.assertThat(location).startsWith("/api/questions");
    }
    
    @Test
    public void list() {
        ResponseEntity<List<Question>> response = template().exchange("/api/questions", HttpMethod.GET, null, new ParameterizedTypeReference<List<Question>>(){});
        List<Question> questions = response.getBody();
//        softly.assertThat(questions.size()).isEqualTo(2);
        log.debug("message : {}", questions);
    }
    
    @Test
    public void show() {
        ResponseEntity<Question> response = template().getForEntity("/api/questions/1", Question.class);
        Question question = response.getBody();
        softly.assertThat(question.getId()).isEqualTo(1);
        softly.assertThat(question.getTitle()).isEqualTo("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?");
        log.debug("message : {}", question);
    }
    
    @Test
    public void update_owner() {
        User newUser = defaultUser();
        Question question = new Question("제목입니다.", "내용입니다.");
        String location = createResource(basicAuthTemplate(), "/api/questions", question);
        question = getResource(location, Question.class, newUser);
        
        Question updatedQuestion = new Question(question.getId(), "제목입니다.2", question.getContents(),question.getWriter());
        
        ResponseEntity<Question> responseEntity = basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);
        
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updatedQuestion.equalsTitleAndContentsAndWriter(responseEntity.getBody())).isTrue();
    }
    
    @Test
    public void update_no_owner() {
        User newUser = findByUserId("sanjigi");
        Question question = new Question("제목입니다.", "내용입니다.");
        String location = createResource(basicAuthTemplate(newUser), "/api/questions", question);
        question = getResource(location, Question.class, newUser);
        
        Question updatedQuestion = new Question(question.getId(), "제목입니다.2", question.getContents(),question.getWriter());
        
        ResponseEntity<Question> responseEntity = basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        log.debug("error message : {}", responseEntity.getBody());
    }
    @Test
    public void update_no_login() {
        User newUser = defaultUser();
        Question question = new Question("제목입니다.", "내용입니다.");
        String location = createResource(basicAuthTemplate(), "/api/questions", question);
        question = getResource(location, Question.class, newUser);
        
        Question updatedQuestion = new Question(question.getId(), "제목입니다.2", question.getContents(),question.getWriter());
        
        ResponseEntity<Question> responseEntity = template().exchange(location, HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }
    
    @Test
    public void delete_owner() {
        User newUser = defaultUser();
        Question question = new Question("제목입니다.", "내용입니다.");
        String location = createResource(basicAuthTemplate(), "/api/questions", question);
        question = getResource(location, Question.class, newUser);
        
        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange(location, HttpMethod.DELETE, null, Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
    
    @Test
    public void delete_no_owner() {
        User newUser = findByUserId("sanjigi");
        Question question = new Question("제목입니다.", "내용입니다.");
        String location = createResource(basicAuthTemplate(newUser), "/api/questions", question);
        question = getResource(location, Question.class, newUser);
        
        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange(location, HttpMethod.DELETE, null, Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    
    @Test
    public void delete_no_login() {
        User newUser = defaultUser();
        Question question = new Question("제목입니다.", "내용입니다.");
        String location = createResource(basicAuthTemplate(newUser), "/api/questions", question);
        question = getResource(location, Question.class, newUser);
        
        ResponseEntity<Void> responseEntity = template().exchange(location, HttpMethod.DELETE, null, Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
