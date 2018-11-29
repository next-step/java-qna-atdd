package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Answer;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.AnswerService;
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
    @Resource(name = "answerService")
    private AnswerService answerService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser,@PathVariable long questionId, @Valid @RequestBody Answer contents) {
        Answer answer = answerService.addAnswer(loginUser, questionId, contents.getContents());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + answer.generateUrl()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }


    @GetMapping("{id}")
    public Answer show(@PathVariable long id) {
        return answerService.findByAnswerId(id);
    }

    @PutMapping("{id}")
    public Answer update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Answer answer) {
        return answerService.updateAnswer(loginUser, id, answer.getContents());
    }

    @DeleteMapping("{id}")
    public Answer delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        return answerService.deleteAnswer(loginUser, id);
    }
}
