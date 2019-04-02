package nextstep.web;

import nextstep.UnAuthenticationException;
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
    public ResponseEntity<Void> create(
        @LoginUser User loginUser, @Valid @RequestBody Question question) {
        Question savedQuestion = qnaService.create(loginUser, question);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + savedQuestion.getId()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public Question show(@PathVariable long id) throws UnAuthenticationException {
        return qnaService.findById(id).orElseThrow(UnAuthenticationException::new);
    }

    @PutMapping("{id}")
    public Question update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Question updateQuestion) throws UnAuthenticationException {
        return qnaService.update(loginUser, id, updateQuestion);
    }

    @DeleteMapping("{id}")
    public Question delete(@LoginUser User loginUser, @PathVariable long id) throws UnAuthenticationException {
        return qnaService.deleteQuestion(loginUser, id);
    }


}

