package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.QuestionNotFoundException;
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
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User user, @Valid @RequestBody Question question) {
        Question saved = qnaService.create(user, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + saved.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("")
    public List<Question> showAllQuestions() {
        return qnaService.findAllQuestions();
    }

    @GetMapping("{id}")
    public Question showQuestionById(@PathVariable long id) {
        return qnaService.findById(id).orElseThrow(QuestionNotFoundException::new);
    }

    @PostMapping("{id}")
    public Question update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Question updated) {
        return qnaService.update(loginUser, id, updated);
    }

    @DeleteMapping("{id}")
    public Question delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        return qnaService.deleteQuestion(loginUser, id);
    }
}
