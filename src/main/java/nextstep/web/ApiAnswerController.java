package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Answer;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    @Autowired
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity create(@LoginUser User loginUser, @PathVariable long questionId, @Valid @RequestBody Answer answer) {
        Answer createdAnswer = qnaService.addAnswer(loginUser, questionId, answer);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/" + createdAnswer.generateUrl()));
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public Answer show(@PathVariable long questionId, @PathVariable long id) {
        return qnaService.findAnswer(questionId, id);
    }

    @PutMapping("/{id}")
    public Answer update(@LoginUser User loginUser, @PathVariable long questionId, @PathVariable long id, @Valid @RequestBody Answer answer) {
        return qnaService.updateAnswer(loginUser, questionId, id, answer);
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@LoginUser User loginUser, @PathVariable long questionId, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteAnswer(loginUser, questionId, id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
