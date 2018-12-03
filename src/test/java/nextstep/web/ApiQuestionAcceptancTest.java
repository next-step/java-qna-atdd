package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptancTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptancTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void create() {

        String title = "test 입니다.";
        String contents = "테스트 내용 입니다.";
        Question question = new Question(title,contents);

        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", question, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        softly.assertThat(questionRepository.findById(question.getId())).isNotNull();
    }

    @Test
    public void update_logined() {
        Question updatedQuetion = new Question("update1", "updatedTest1");
        //id - 1 , title - 국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까 , writer - javajigi
        ResponseEntity<Question> responseEntity =
                basicAuthTemplate().exchange("/api/questions/1",HttpMethod.PUT, createHttpEntity(updatedQuetion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void update_noLogined() {
        User sanjigi = findByUserId("sanjigi");
        Question updatedQuetion = new Question("update2", "updatedTest2");
        //id - 1 , title - 국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까
        ResponseEntity<Question> responseEntity =
                basicAuthTemplate(sanjigi).exchange("/api/questions/1",HttpMethod.PUT, createHttpEntity(updatedQuetion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void getQuestion(){
        ResponseEntity<Question> responseEntity =
                basicAuthTemplate().exchange("/api/questions/1",HttpMethod.GET, createHttpEntity(1), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getBody().getId()).isEqualTo(1L);
    }

    @Test
    public void deleteQuestion(){
        ResponseEntity<Question> responseEntity =
                basicAuthTemplate().exchange("/api/questions/1",HttpMethod.DELETE, createHttpEntity(1), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
