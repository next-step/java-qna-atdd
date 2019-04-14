package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions")
public class ApiQnaController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping
    public Iterable<Question> list(
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC, size = 20) Pageable pageable) {

        return qnaService.findAll(pageable);
    }

    @PostMapping
    public ResponseEntity<Void> createQuestion(@LoginUser User loginUser, @Valid @RequestBody Question question) {
        Question createdQuestion = qnaService.create(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + createdQuestion.generateUrl()));

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public Question detailQuestion(@PathVariable long id) {
        return qnaService.findById(id);
    }

    @PutMapping("/{id}")
    public Question updateQuestion(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Question question) {
        return qnaService.update(loginUser, id, question);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteQuestion(@LoginUser User loginUser, @PathVariable long id) throws nextstep.CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<Answer> showAnswer(@PathVariable Long questionId, @PathVariable Long answerId) {
        Answer answer = qnaService.findAnswer(answerId);

        return ResponseEntity.ok(answer);
    }

    @PostMapping("/{questionId}/answers")
    public ResponseEntity<Void> addAnswer(@LoginUser User loginUser, @PathVariable Long questionId, @Valid @RequestBody Answer answer) {
        Answer createAnswer = qnaService.addAnswer(loginUser, questionId, answer);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + createAnswer.generateUrl()));

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<Void> deleteAnswer(@LoginUser User loginUser, @PathVariable Long questionId, @PathVariable Long answerId) throws CannotDeleteException {
        qnaService.deleteAnswer(loginUser, answerId);
        return new ResponseEntity(HttpStatus.OK);
    }
}
