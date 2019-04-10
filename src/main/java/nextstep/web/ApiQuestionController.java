package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.dto.QuestionDto;
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
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody  Question question) throws Exception {
        Question savedQuestion = qnaService.create(loginUser, question);
        return ResponseEntity.created(URI.create("/api/" + savedQuestion.generateUrl())).build();
    }

    @GetMapping("")
    public ResponseEntity<List<Question>> showAll() {
        return ResponseEntity.ok(qnaService.findQuestions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> showOne(@PathVariable long id) {
        return ResponseEntity.ok(qnaService.findQuestion(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> update(@LoginUser User loginUser, @PathVariable long id, @RequestBody QuestionDto updatedQuestionDto) throws CannotUpdateException {
        Question updated = qnaService.updateQuestion(loginUser, id, updatedQuestionDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return ResponseEntity.ok().build();
    }
}
