package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.User;
import nextstep.exception.CannotDeleteException;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    @Resource
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable Long questionId, @RequestBody String contents) {
        Answer newAnswer = qnaService.addAnswer(loginUser, questionId, contents);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/" + newAnswer.generateUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{answerId}")
    public Answer show(@PathVariable Long answerId) {
        return qnaService.findByAnswerId(answerId);
    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable Long questionId, @PathVariable Long answerId) throws CannotDeleteException {
        Answer deletedAnswer = qnaService.deleteAnswer(loginUser, answerId);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/" + deletedAnswer.getQuestion().generateUrl()));
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
