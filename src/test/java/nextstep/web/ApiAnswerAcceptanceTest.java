package nextstep.web;

import nextstep.UnAuthorizedException;
import nextstep.domain.dto.AnswerResponseDto;
import nextstep.domain.entity.Answer;
import nextstep.domain.entity.Question;
import nextstep.domain.repository.AnswerRepository;
import nextstep.domain.repository.QuestionRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    private static final String QUESTION_PATH = "/api/questions";
    private Question testQuestion;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Before
    public void setUp() throws Exception {
        testQuestion = new Question("newTitle", "newContents");
    }

    @Test
    public void create() {
        String location = createResourceWithLogin(QUESTION_PATH, testQuestion, defaultUser());
        Question savedQuestion = getResourceWithLogin(location, Question.class, defaultUser());
        softly.assertThat(savedQuestion).isNotNull();

        String contents = "댓글입니다";
        String answerLocation = location + "/answers";
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity(answerLocation, contents, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void create_no_login() {
        String location = testQuestion.generateApiUrl() + "/answers";
        String contents = "댓글입니다";
        ResponseEntity<Void> response = template().postForEntity(location, contents, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // [JPA] OneToMany 오류
    // https://ankonichijyou.tistory.com/entry/JPA-OneToMany-%EC%98%A4%EB%A5%98
    @Test
    public void show() {
        Answer newAnswer = answerRepository.findById(1L).orElseThrow(UnAuthorizedException::new);

        String location = newAnswer.generateApiUrl();
        AnswerResponseDto savedAnswer = getResourceWithLogin(location, AnswerResponseDto.class, defaultUser());
        softly.assertThat(savedAnswer).isNotNull();
    }

    @Test
    public void delete() {
        Answer newAnswer = answerRepository.findById(1L).orElseThrow(UnAuthorizedException::new);
        String location = newAnswer.generateApiUrl();
        AnswerResponseDto savedAnswer = getResourceWithLogin(location, AnswerResponseDto.class, defaultUser());

        ResponseEntity<AnswerResponseDto> response = basicAuthTemplate()
                .exchange(savedAnswer.generateApiUrl(), HttpMethod.DELETE, createHttpEntity(null), AnswerResponseDto.class);

        softly.assertThat(response.getBody().isDeleted()).isTrue();
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);

    }
}
