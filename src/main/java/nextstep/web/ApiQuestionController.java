package nextstep.web;

import lombok.RequiredArgsConstructor;
import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import nextstep.web.dto.QuestionRequestDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class ApiQuestionController {
    private static final String REST_BASE_URL = "/api";

    private final QnaService qnaService;

    @PostMapping
    public ResponseEntity<Void> create(@LoginUser User loginUser, @RequestBody Question question) {
        Question savedQuestion = qnaService.createQuestion(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(REST_BASE_URL + savedQuestion.generateUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Question> list(@RequestParam(required = false, defaultValue = "1") int page,
                               @RequestParam(required = false, defaultValue = "20") int size) {
        return qnaService.findAll(page, size);
    }

    @GetMapping("/{id}")
    public Question show(@PathVariable long id) {
        return qnaService.findQuestionById(id);
    }

    @PutMapping("/{id}")
    public Question update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody QuestionRequestDTO updatedQuestionDto) {
        Question updatedQuestion = Question.of(updatedQuestionDto);
        return qnaService.updateQuestion(loginUser, id, updatedQuestion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long id) {
        qnaService.deleteQuestion(loginUser, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{questionId}/answers")
    public ResponseEntity<Answer> addAnswer(@PathVariable long questionId, @RequestBody String contents, @LoginUser User loginUser) {
        Answer createdAnswer = qnaService.createAnswer(loginUser, questionId, contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(REST_BASE_URL + createdAnswer.generateUrl()));
        return new ResponseEntity<>(createdAnswer, headers, HttpStatus.CREATED);
    }
}
