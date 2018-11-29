package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
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

    @GetMapping("/{id}")
    public Question get(@PathVariable long id) {
        return qnaService.findById(id);
    }

    @PostMapping("")
    public ResponseEntity create(@LoginUser User loginUser,
                                 @Valid @RequestBody Question question) {
        Question created = qnaService.create(loginUser, question);

        return ResponseEntity.created(URI.create("/api" + created.generateUrl())).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@LoginUser User loginUser,
                                 @PathVariable long id,
                                 @Valid @RequestBody Question updateQuestion) {
        Question updated = qnaService.update(loginUser, id, updateQuestion);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@LoginUser User loginUser, @PathVariable long id) {
        qnaService.deleteQuestion(loginUser, id);
    }
}
