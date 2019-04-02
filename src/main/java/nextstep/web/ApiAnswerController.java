package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import nextstep.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable long questionId, @RequestBody String contents) {
        Answer savedAnswer = qnaService.addAnswer(loginUser, questionId, contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + savedAnswer.generateUrl()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public Answer show(@PathVariable long questionId, @PathVariable long id) {
        return qnaService.findAnswerById(questionId, id);
    }

    @PutMapping("{id}")
    public Answer update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Answer updatedAnswer) {
        return qnaService.updateAnswer(loginUser, id, updatedAnswer);
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@LoginUser User loginUser, @PathVariable long questionId, @PathVariable long id) {
        try {
            qnaService.deleteAnswer(loginUser, questionId, id);
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }
}
