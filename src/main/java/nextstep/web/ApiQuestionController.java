package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.CannotFoundException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> createQuestion(@LoginUser User user, @Valid @RequestBody Question question) {
        Question createQuestion = qnaService.createQuestion(user, question);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + createQuestion.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public Question show(@PathVariable long id) {
        return qnaService.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    @PutMapping("/{id}")
    public Question updateQuestion(@LoginUser User user, @PathVariable("id") long id, Question question) {
        return qnaService.updateQuestion(user, id ,question);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@LoginUser User user, @PathVariable("id") long id) throws CannotDeleteException {
        qnaService.deleteQuestion(user, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/answer")
    public ResponseEntity<Void> createAnswer(@LoginUser User user, @PathVariable("id") long id, String contents) throws CannotFoundException {
        qnaService.addAnswer(user, id, contents);
        Question question = qnaService.findByIdAndDeletedFalse(user ,id);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + question.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/answer/{answerId}")
    public ResponseEntity<Void> deleteAnswer(@LoginUser User user, @PathVariable("id") long id , @PathVariable("answerId") long answerId) throws CannotFoundException, CannotDeleteException {
        log.info("Question deleteAnswer method answerId : " + answerId + "  user : " + user + "  id : " + id);
        qnaService.deleteAnswer(user, id, answerId);
        return ResponseEntity.ok().build();
    }



}
