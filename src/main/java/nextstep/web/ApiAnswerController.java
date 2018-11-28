package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Answer;
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
public class ApiAnswerController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("{questionId}/answers")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable long questionId, @Valid @RequestBody Answer answer) {
        Answer savedAnswer = qnaService.createAnswer(loginUser, questionId, answer);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + savedAnswer.generateUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{questionId}/answers")
    public Answer show(@PathVariable long questionId) {
        return qnaService.findAnswer(questionId);
    }

    @PutMapping("{questionId}/answers/{answerId}")
    public Answer update(@LoginUser User loginUser, @PathVariable long questionId, @PathVariable long answerId, @Valid @RequestBody Answer answer) {
        return qnaService.updateAnswer(loginUser, questionId, answerId, answer);
    }

    @DeleteMapping("{questionId}/answers/{answerId}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long questionId, @PathVariable long answerId) throws CannotDeleteException {
        qnaService.deleteAnswer(loginUser, questionId, answerId);
        return ResponseEntity.ok().build();
    }
}
