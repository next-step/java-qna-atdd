package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
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

    @PostMapping
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody Question question) {
        Question savedQuestion = qnaService.create(loginUser, question);
        URI location = URI.create("/api/questions/" + savedQuestion.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("{id}")
    public Question show(@PathVariable long id) {
        return qnaService.findOne(id);
    }

    @PutMapping("{id}")
    public Question update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Question question) {
        return qnaService.update(loginUser, id, question);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.delete(loginUser, id);
        return ResponseEntity.ok().build();
    }
}
