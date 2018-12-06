package nextstep.web;


import nextstep.CannotDeleteException;
import nextstep.UnAuthenticationException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, Question question) {
        Question newQuestion = qnaService.create(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + newQuestion.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("")
    public List<Question> showQuestionList(Pageable pageable) {
        return qnaService.findAll(pageable);
    }

    @GetMapping("{id}")
    public Question showQuestionDetail(@PathVariable long id) {
        return qnaService.findByQuestionId(id);
    }

    @DeleteMapping("{id}")
    public Question delete(@LoginUser User loginUser, @PathVariable long id) throws UnAuthenticationException, CannotDeleteException {
        return qnaService.deleteQuestion(loginUser, id);

    }

    @PutMapping("{id}")
    public Question update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Question updateQuestion) throws UnAuthenticationException {
        return qnaService.update(loginUser, id, updateQuestion);
    }
}
