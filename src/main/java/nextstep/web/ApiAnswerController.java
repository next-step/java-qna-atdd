package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Answer;
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
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerController.class);
    private static final String API = "/api";

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@Valid @RequestBody Answer answer, @LoginUser User loginUser, @PathVariable Long questionId) {
        Answer savedAnswer = qnaService.addAnswer(loginUser, questionId, answer);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(API + savedAnswer.generateUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public Answer show(@PathVariable Long id) {
        return qnaService.findByIdAnswer(id);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable Long id) {
        HttpHeaders headers = new HttpHeaders();
        try {
            qnaService.deleteAnswer(loginUser, id);
        }catch (CannotDeleteException e) {
            return new ResponseEntity<>(headers, HttpStatus.FORBIDDEN);
        }
        headers.setLocation(URI.create("/"));
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

}
