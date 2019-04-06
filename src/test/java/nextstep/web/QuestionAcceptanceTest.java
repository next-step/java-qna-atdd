package nextstep.web;

import nextstep.domain.AnswerRepository;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.helper.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import javax.persistence.EntityNotFoundException;


public class QuestionAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    public void createForm() throws Exception {
        // When
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 질문_생성() throws Exception {
        // Given
        User loginUser = defaultUser();
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
            .addParameter("title", "제목입니다.")
            .addParameter("contents", "제목입니다.")
            .build();

        // When
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/questions", request, String.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }

    @Test
    public void 전체_리스트_조회() throws Exception {
        // When
        ResponseEntity<String> response = template().getForEntity("/", String.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());

        Question question = questionRepository.findById(1L)
            .orElseThrow(EntityNotFoundException::new);

        softly.assertThat(response.getBody()).contains(question.generateUrl());
        softly.assertThat(response.getBody()).contains(question.getTitle());
    }

    @Test
    public void 질문_조회() throws Exception {
        // Given
        Question question = questionRepository.findAll().get(0);

        // When
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", question.getId()), String.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());

        softly.assertThat(response.getBody()).contains(question.generateUrl());
        softly.assertThat(response.getBody()).contains(question.getTitle());
    }

    @Test
    public void 조회_불가_없는_질문_400() throws Exception {
        // When
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", 100L), String.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 수정_페이지_권한_없음_401() throws Exception {
        // Given
        Question question = questionRepository.findAll().get(0);

        // When
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", question.getId()),
            String.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 수정_페이지_없는_질문_400() throws Exception {
        // Given
        User loginUser = defaultUser();

        // When
        ResponseEntity<String> response = basicAuthTemplate(loginUser).getForEntity(String.format("/questions/%d/form", 100L),
            String.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 수정_페이지_본인_외에_접근_불가_403() throws Exception {
        // Given
        User loginUser = findByUserId("njkim");
        Question question = questionRepository.findById(1L)
            .orElseThrow(EntityNotFoundException::new);

        // When
        ResponseEntity<String> response = basicAuthTemplate(loginUser).getForEntity(String.format("/questions/%d/form", question.getId()),
            String.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 질문_수정_페이지() throws Exception {
        // Given
        User loginUser = defaultUser();
        Question question = questionRepository.findById(1L)
            .orElseThrow(EntityNotFoundException::new);

        // When
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
            .getForEntity(String.format("/questions/%d/form", question.getId()), String.class);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains(question.getTitle());
        softly.assertThat(response.getBody()).contains(question.getContents());
    }

    @Test
    public void 수정_불가_권한_없음_401() throws Exception {
        // Given
        Question question = questionRepository.findById(1L)
            .orElseThrow(EntityNotFoundException::new);

        // When
        ResponseEntity<String> response = update(template(), question.getId());

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 수정_불가_질문_없음_400() throws Exception {
        // Given
        User loginUser = defaultUser();

        // When
        ResponseEntity<String> response = update(basicAuthTemplate(loginUser), 100L);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 수정_불가_본인_아님_403() throws Exception {
        // Given
        User loginUser = findByUserId("njkim");
        Question question = questionRepository.findById(2L)
            .orElseThrow(EntityNotFoundException::new);

        // When
        ResponseEntity<String> response = update(basicAuthTemplate(loginUser), question.getId());

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 수정_성공() throws Exception {
        // Given
        User loginUser = findByUserId("sanjigi");
        Question question = questionRepository.findById(loginUser.getId())
            .orElseThrow(EntityNotFoundException::new);

        // When
        ResponseEntity<String> response = update(basicAuthTemplate(loginUser), question.getId());

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
//        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");

        Question updateQuestion = questionRepository.findById(2L)
            .orElseThrow(EntityNotFoundException::new);
        softly.assertThat(updateQuestion.getTitle()).isEqualTo("제목 수정");
        softly.assertThat(updateQuestion.getContents()).isEqualTo("내용 수정");
    }

    private ResponseEntity<String> update(TestRestTemplate template, long id) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
            .put()
            .addParameter("title", "제목 수정")
            .addParameter("contents", "내용 수정")
            .build();

        return template.postForEntity(String.format("/questions/%d", id), request, String.class);
    }

    @Test
    public void 삭제_불가_권한_없음_401() throws Exception {
        // Given
        Question question = questionRepository.findById(1L)
            .orElseThrow(EntityNotFoundException::new);

        // When
        answerRepository.deleteAll();
        ResponseEntity<String> response = delete(template(), question.getId());

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 삭제_불가_없는_질문_400() throws Exception {
        // Given
        User loginUser = defaultUser();

        // When
        answerRepository.deleteAll();
        ResponseEntity<String> response = delete(basicAuthTemplate(loginUser), 100L);

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 삭제_불가_본인_외에_405() throws Exception {
        // Given
        User loginUser = findByUserId("njkim");
        Question question = questionRepository.findById(1L)
            .orElseThrow(EntityNotFoundException::new);

        // When
        answerRepository.deleteAll();
        ResponseEntity<String> response = delete(basicAuthTemplate(loginUser), question.getId());

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }


    @Test
    public void 삭제_성공() throws Exception {
        // Given
        User loginUser = defaultUser();
        Question question = questionRepository.findById(1L)
            .orElseThrow(EntityNotFoundException::new);


        //When
        answerRepository.deleteAll();
        ResponseEntity<String> response = delete(basicAuthTemplate(loginUser), question.getId());

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
        softly.assertThat(questionRepository.findById(1L).orElseThrow(EntityNotFoundException::new).isDeleted()).isTrue();
    }

    private ResponseEntity<String> delete(TestRestTemplate testRestTemplate, long id) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
            .delete()
            .build();

        return testRestTemplate
            .postForEntity(String.format("/questions/%d", id), request, String.class);
    }

    @Test
    public void 삭제_후_조회_불가() throws Exception {
        // Given
        User loginUser = defaultUser();
        Question question = questionRepository.findById(3L)
            .orElseThrow(EntityNotFoundException::new);

        //When
        answerRepository.deleteAll();
        ResponseEntity<String> response = delete(basicAuthTemplate(loginUser), question.getId());

        // Then
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
        softly.assertThat(questionRepository
            .findAllByDeleted(false, PageRequest.of(1, 10)).size()).isEqualTo(0);
    }
}