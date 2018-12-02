package nextstep.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.CannotDeleteException;
import nextstep.domain.Question;
import nextstep.domain.QuestionBody;
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

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody QuestionBody question) {
        Question savedQuestion = qnaService.createQuestion(loginUser, question);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + savedQuestion.generateUrl()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public Question show(@PathVariable long id) {
        return qnaService.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @PutMapping("/{id}")
    public Question update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody QuestionBody updatedQuestion) {
        return qnaService.updateQuestion(loginUser, id, updatedQuestion);
    }
    @DeleteMapping("/{id}")
    public Question delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        return qnaService.deleteQuestion(loginUser, id);
    }

}
