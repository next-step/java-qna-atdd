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
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser,@PathVariable long questionId, @Valid @RequestBody Answer contents) {
        Answer answer = qnaService.addAnswer(loginUser, questionId, contents.getContents());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + answer.generateUrl()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }


    @GetMapping("{id}")
    public Answer show(@PathVariable long id) {
        return qnaService.findByAnswerId(id);
    }

    @PutMapping("{id}")
    public Answer update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Answer answer) {
        System.out.println("id : "+id);
        return qnaService.updateAnswer(loginUser, id, answer.getContents());
    }


    @DeleteMapping("{id}")
    public Answer delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        System.out.println("id : "+id);
        return qnaService.deleteAnswer(loginUser, id);
    }
}
