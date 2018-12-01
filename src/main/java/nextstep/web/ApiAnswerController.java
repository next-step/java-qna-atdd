package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/{answerId}")
    public ResponseEntity get(@PathVariable long questionId,
                              @PathVariable long answerId) {
        return ResponseEntity.ok(qnaService.findAnswerById(questionId, answerId));
    }

    @PostMapping("")
    public ResponseEntity create(@LoginUser User loginUser,
                                 @PathVariable long questionId,
                                 @RequestBody String contents) {
        Answer created = qnaService.addAnswer(loginUser, questionId, contents);

        return ResponseEntity.created(URI.create("/api" + created.generateUrl())).build();
    }

    @PutMapping("/{answerId}")
    public ResponseEntity update(@LoginUser User loginUser,
                                 @PathVariable long questionId,
                                 @PathVariable long answerId,
                                 @RequestBody String contents) {
        Answer updated = qnaService.updateAnswer(loginUser, questionId, answerId, contents);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{answerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@LoginUser User loginUser,
                       @PathVariable long questionId,
                       @PathVariable long answerId) {
        qnaService.deleteAnswer(loginUser, questionId, answerId);
    }
}
