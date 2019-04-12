package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.exception.CannotDeleteException;
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
        Question savedQuestion = qnaService.create(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + savedQuestion.generateUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping
    public Iterable<Question> showAll() {
        return qnaService.findAll();
    }

    @GetMapping("/{id}")
    public Question show(@PathVariable long id) {
        return qnaService.findByQuestionId(id);
    }

    @PutMapping("/{id}")
    public Question update(@LoginUser User loginUser, @PathVariable Long id, @RequestBody Question updateQuestion) {
        return qnaService.update(loginUser, id, updateQuestion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable Long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions"));
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
