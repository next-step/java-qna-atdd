package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.exception.ResourceNotFoundException;
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

    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping
    public ResponseEntity<Void> create(@LoginUser final User loginUser,
                                       @Valid @RequestBody final Question question) {

        final Question saveQuestion = qnaService.create(loginUser, question);

        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + saveQuestion.getId()));
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public Question detail(@PathVariable final long id) {
        return qnaService.findById(id);
    }

    @PutMapping("/{id}")
    public Question update(@LoginUser final User loginUser,
                           @PathVariable final long id,
                           @Valid @RequestBody final Question question) {
        return qnaService.update(loginUser, id, question);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@LoginUser final User loginUser,
                                       @PathVariable final long id) {
        try {
            qnaService.delete(loginUser, id);
        } catch (final ResourceNotFoundException | CannotDeleteException e) {
            log.debug("Error message : {}", e);
            return new ResponseEntity<>(new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(new HttpHeaders(), HttpStatus.NO_CONTENT);
    }


}
