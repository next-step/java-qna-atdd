package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Answer;
import nextstep.domain.AnswerRepository;
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
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {

    private static final Logger log = LoggerFactory.getLogger(ApiAnswerController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @PostMapping
    public final ResponseEntity<Void> create(@LoginUser final User loginUser,
                                             @PathVariable final long questionId,
                                             @Valid @RequestBody final String contents) {

        final Answer saveAnswer = qnaService.addAnswer(loginUser, questionId, contents);

        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + saveAnswer.generateUrl()));
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    @SuppressWarnings("unused")
    @GetMapping("/{id}")
    public final Answer detail(@PathVariable final long questionId,
                               @PathVariable final long id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found answer"));
    }

    @DeleteMapping("/{id}")
    public final ResponseEntity<Void> delete(@LoginUser final User loginUser,
                                             @PathVariable final long id) {
        try {
            qnaService.deleteAnswer(loginUser, id);
        } catch (final ResourceNotFoundException | CannotDeleteException e) {
            log.debug("Error message : {}", e);
            return new ResponseEntity<>(new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(new HttpHeaders(), HttpStatus.NO_CONTENT);
    }

}