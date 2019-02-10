package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.UnAuthenticationException;
import nextstep.UnAuthorizedException;
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

@RequestMapping("/api/questions/{id}/answers")
@RestController
public class ApiAnswerController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping
    public ResponseEntity<Answer> add(@LoginUser User user, @PathVariable long id, @Valid @RequestBody Answer answer) throws UnAuthenticationException {
        Answer addedAnswer = qnaService.addAnswer(user, id, answer);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + id + "/answers/" + answer.getId()));

        return new ResponseEntity<>(addedAnswer, headers, HttpStatus.CREATED);
    }

    @GetMapping
    public Answer show(@PathVariable long id) {
        return qnaService.findByAnswerId(id).get();
    }

    @PutMapping("{answerId}")
    public Answer update(@LoginUser User loginUser, @PathVariable long answerId, @Valid @RequestBody Answer updatedAnswer) {
        if(!qnaService.findByAnswerId(answerId).get().isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        return qnaService.updateAnswer(loginUser, answerId, updatedAnswer);
    }

    @DeleteMapping("{answerId}")
    public ResponseEntity<Answer> delete(@LoginUser User loginUser, @PathVariable long id, @PathVariable long answerId) throws CannotDeleteException {
        Answer deleteAnswer = qnaService.deleteAnswer(loginUser, answerId);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + id + "/answers/" + answerId));

        if(!qnaService.findByAnswerId(answerId).get().isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        return new ResponseEntity<>(deleteAnswer, headers, HttpStatus.OK);
    }
}
