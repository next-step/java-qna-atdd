package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/answers")
public class ApiAnswerController {
    private final QnaService qnaService;

    public ApiAnswerController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping("/{answerId}")
    public Answer show(@PathVariable long answerId) {
        return qnaService.findAnswerById(answerId);
    }

    @PostMapping
    public ResponseEntity<Void> createAnswer(@LoginUser User loginUser, long questionId, @Valid Answer answer) {
        Answer savedAnswer = qnaService.addAnswer(loginUser, questionId, answer.getContents());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(savedAnswer.generateUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity<Void> deleteAnswer(@LoginUser User loginUser, @PathVariable long answerId) {
        qnaService.deleteAnswer(loginUser, answerId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{answerId}")
    public Answer updateAnswer(@LoginUser User loginUser, @PathVariable long answerId, @RequestBody @Valid Answer answer) {
        return qnaService.updateAnswer(loginUser, answerId, answer);
    }
}
