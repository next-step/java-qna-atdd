package nextstep.web;

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
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(
            @LoginUser User loginUser, @PathVariable Long questionId,
            @RequestBody String contents
    ) {
        Answer resultAnswer = qnaService.addAnswer(loginUser, questionId, contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(resultAnswer.generateUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{answerId}")
    public ResponseEntity<Answer> get(@PathVariable Long questionId, @PathVariable Long answerId) {
        Answer answer = qnaService.findAnswerById(questionId, answerId);
        return ResponseEntity.ok(answer);
    }

    @PutMapping("{answerId}")
    public ResponseEntity<Answer> modify(
            @LoginUser User loginUser, @PathVariable Long questionId,
            @PathVariable Long answerId, @Valid @RequestBody String contents) {

        Answer answer = qnaService.updateAnswer(loginUser, questionId, answerId, contents);
        return ResponseEntity.ok(answer);
    }

    @DeleteMapping("{answerId}")
    public ResponseEntity<Void> delete(
            @LoginUser User loginUser, @PathVariable Long questionId,
            @PathVariable Long answerId
    ) {
        qnaService.deleteAnswer(loginUser, questionId, answerId);
        return ResponseEntity.ok().build();
    }

}
