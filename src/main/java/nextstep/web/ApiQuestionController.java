package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
    private final QnaService qnaService;

    public ApiQuestionController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping("/{id}")
    public Question getQuestion(@PathVariable long id) {
        return qnaService.findById(id)
                .orElseThrow(IllegalArgumentException::new);
    }

    @PostMapping
    public ResponseEntity<Void> createQuestion(@LoginUser User loginUser, @RequestBody @Valid Question question) {
        Question createdQuestion = qnaService.create(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(createdQuestion.generateRestUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public Question updateQuestion(@LoginUser User loginUser, @PathVariable long id, @RequestBody @Valid Question question) {
        return qnaService.update(loginUser, id, question);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);

        return ResponseEntity.noContent().build();
    }
}
