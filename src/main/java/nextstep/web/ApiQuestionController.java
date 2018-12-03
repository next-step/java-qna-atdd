package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionController.class);
    private static final String API = "/api";

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@Valid @RequestBody Question question, @LoginUser User loginUser) {
        Question savedQuestions = qnaService.create(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(API + savedQuestions.generateUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public Question show(@PathVariable Long id) {
        return qnaService.findById(id);
    }

    @PutMapping("{id}")
    public Question update(@Valid @RequestBody Question updatedQuestion, @PathVariable Long id, @LoginUser User loginUser) {
        return qnaService.update(loginUser, id, updatedQuestion);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @LoginUser User loginUser) {
        HttpHeaders headers = new HttpHeaders();
        try {
            qnaService.deleteQuestion(loginUser, id);
        }catch (CannotDeleteException e) {
            return new ResponseEntity<>(headers, HttpStatus.FORBIDDEN);
        }
        headers.setLocation(URI.create("/"));
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
