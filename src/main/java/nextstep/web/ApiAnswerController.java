package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import nextstep.domain.Answer;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {

    @Autowired
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable long questionId, @RequestBody String contents) {
        Answer answer = qnaService.createAnswer(loginUser, questionId, contents);
        return ResponseEntity.created(URI.create("/api/" +  answer.generateUrl())).build();
    }

    @GetMapping("")
    public ResponseEntity<List<Answer>> showAll(@PathVariable long questionId) {
        return ResponseEntity.ok(qnaService.findAnswers(questionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Answer> showOne(@PathVariable long id) {
        return ResponseEntity.ok(qnaService.findAnswer(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Answer> update(@LoginUser User loginUser, @PathVariable long id, @RequestBody String contents) throws CannotUpdateException {
        return ResponseEntity.ok(qnaService.updateAnswer(loginUser, id, contents));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteAnswer(loginUser, id);
        return ResponseEntity.ok().build();
    }
}
