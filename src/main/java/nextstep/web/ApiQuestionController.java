package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody Question question) {
        Question resultQuestion = qnaService.create(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + resultQuestion.getId()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<Question> get(@PathVariable Long id) {
        Question question = qnaService.findById(id);
        return ResponseEntity.ok(question);
    }

    @PutMapping("{id}")
    public ResponseEntity<Question> modify(@LoginUser User loginUser, @PathVariable Long id, @Valid @RequestBody Question question) {
        Question resultQuestion = qnaService.update(loginUser, id, question);
        return ResponseEntity.ok(resultQuestion);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable Long id) {
        qnaService.deleteQuestion(loginUser, id);
        return ResponseEntity.ok().build();
    }

}
