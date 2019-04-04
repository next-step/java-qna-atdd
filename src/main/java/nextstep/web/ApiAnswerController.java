package nextstep.web;

import java.net.URI;
import javax.validation.Valid;
import nextstep.CannotDeleteException;
import nextstep.domain.Answer;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    private final QnaService qnaService;

    public ApiAnswerController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @PostMapping("")
    public ResponseEntity create(@LoginUser User loginUser, @PathVariable long questionId, @Valid @RequestBody Answer answer) {
        Answer createdAnswer = qnaService.addAnswer(loginUser, questionId, answer);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + createdAnswer.generateUrl()));
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
