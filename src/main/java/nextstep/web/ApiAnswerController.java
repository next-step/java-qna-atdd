package nextstep.web;

import nextstep.CannotDeleteException;
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
@RequestMapping("/api/answers")
public class ApiAnswerController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("questions/{id}")
    public ResponseEntity<Void> create(@LoginUser User user, @PathVariable long id, @Valid @RequestBody String answer) {
        Answer saved = qnaService.addAnswer(user, id, answer);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/answers/" + saved.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public Answer showAnswer(@PathVariable long id) {
        return qnaService.findAnswerById(id);
    }

    @DeleteMapping("{id}")
    public Answer delete(@LoginUser User user, @PathVariable long id) throws CannotDeleteException {
        return qnaService.deleteAnswer(user, id);
    }

    @PostMapping("{id}")
    public Answer update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody String content) {
        return qnaService.updateAnswer(loginUser, id, content);
    }
}
