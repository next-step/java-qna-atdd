package nextstep.web;

import nextstep.CannotDeleteException;
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
@RequestMapping("/api/questions")
public class ApiQuestionController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody Question question) {
        Question makedQuestion = qnaService.create(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + makedQuestion.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public Question readQuestion(@PathVariable long id) {
        return qnaService.findById(id).get();
    }

    @PutMapping("{id}")
    public Question updateQuestion(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Question question) {
        return qnaService.update(loginUser, id, question);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteQuestion(@LoginUser User loginUser, @PathVariable long id) {
        HttpHeaders headers = new HttpHeaders();
        try {
            qnaService.deleteQuestion(loginUser, id);
        } catch (CannotDeleteException e) {
            return new ResponseEntity<Void>(headers, HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<Void>(headers, HttpStatus.OK);
    }

}
