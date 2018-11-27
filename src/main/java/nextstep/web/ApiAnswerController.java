package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Answer;
import nextstep.domain.Question;
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
@RequestMapping("/api/answers")
public class ApiAnswerController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("{id}")
    public ResponseEntity<Void> addAnswer(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Answer answer) {
        Answer makedAnswer = qnaService.addAnswer(loginUser, id, answer.getContents());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/answers/" + makedAnswer.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public Answer readAnswer(@PathVariable long id) {
        return qnaService.findAnswerById(id);
    }

    @PutMapping("{id}")
    public Answer updateAnswer(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Answer answer) {
        return qnaService.updateAnswer(loginUser, id, answer);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteQuestion(@LoginUser User loginUser, @PathVariable long id) {
        HttpHeaders headers = new HttpHeaders();
        try {
            qnaService.deleteAnswer(loginUser, id);
        } catch (CannotDeleteException e) {
            return new ResponseEntity<Void>(headers, HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<Void>(headers, HttpStatus.OK);
    }
}
