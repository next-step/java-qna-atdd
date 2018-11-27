package nextstep.web;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.User;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);
    
    @Test
    public void create_no_login() {
        String contents = "그건.. 음.. 어.. 아";
        ResponseEntity<Void> response = template().postForEntity("/api/questions/1/answers/", contents, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", response.getBody());
    }
    
    @Test
    public void create_login() {
        String contents = "그건.. 음.. 어.. 아";
        String location = createResource(basicAuthTemplate(), "/api/questions/1/answers/", contents);
        softly.assertThat(location).startsWith("/api/questions/1/answers");
    }
    
    @Test
    public void show() {
        User newUser = defaultUser();
        String contents = "그건.. 음.. 어.. 아";
        String location = createResource(basicAuthTemplate(), "/api/questions/1/answers/", contents);
        
        Answer answer = getResource(location, Answer.class, newUser);
        log.debug("error message : {}", answer);
        
    }
    
    @Test
    public void delete_owner() {
        User newUser = defaultUser();
        String contents = "그건.. 음.. 어.. 아";
        String location = createResource(basicAuthTemplate(), "/api/questions/1/answers/", contents);
        
        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange(location, HttpMethod.DELETE, null, Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void delete_no_login() {
        User newUser = defaultUser();
        String contents = "그건.. 음.. 어.. 아";
        String location = createResource(basicAuthTemplate(), "/api/questions/1/answers/", contents);
        
        ResponseEntity<Void> responseEntity = template().exchange(location, HttpMethod.DELETE, null, Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    
    @Test
    public void delete_no_owner() {
        User newUser = findByUserId("sanjigi");
        String contents = "그건.. 음.. 어.. 아";
        String location = createResource(basicAuthTemplate(newUser), "/api/questions/1/answers/", contents);
        
        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange(location, HttpMethod.DELETE, null, Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
