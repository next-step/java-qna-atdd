package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
    static final Logger log = LoggerFactory.getLogger(ApiQuestionController.class);

    @Autowired
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Question> create(@LoginUser User loginUser, @Valid @RequestBody  Question question) throws Exception {
        Question savedQuestion = qnaService.create(loginUser, question);
        URI location = URI.create("/api/questions/" + savedQuestion.getId());
        return ResponseEntity.created(location).body(savedQuestion);
    }

    @GetMapping("")
    public ResponseEntity<List<Question>> showAll() {
        return ResponseEntity.ok(qnaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> showOne(@PathVariable long id) {
        return ResponseEntity.of(qnaService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> update(@LoginUser User loginUser, @PathVariable long id, @RequestBody Question updatedQuestion) throws CannotUpdateException {
        Question updated = qnaService.update(loginUser, id, updatedQuestion);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return ResponseEntity.ok().build();
    }
}
