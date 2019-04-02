package nextstep.web;


import nextstep.UnAuthenticationException;
import nextstep.domain.Answer;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionId}/answer")
public class ApiAnswerController {


    @Resource(name = "qnaService")
    private QnaService qnaService;


    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable long questionId, @RequestBody String contents) throws UnAuthenticationException {
        Answer answer = qnaService.addAnswer(loginUser, questionId, contents);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + answer.generateUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }


    @GetMapping("/{answerId}")
    public Answer show(@PathVariable long answerId) throws UnAuthenticationException {
        return qnaService.findByAnswerId(answerId);
    }

    @DeleteMapping("/{answerId}")
    public Answer delete(@LoginUser User loginUser, @PathVariable long answerId) throws UnAuthenticationException {
        Answer answer = qnaService.deleteAnswer(loginUser, answerId);
        return answer;
    }

}
