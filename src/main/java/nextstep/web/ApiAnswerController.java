package nextstep.web;

import java.net.URI;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.CannotDeleteException;
import nextstep.domain.Answer;
import nextstep.domain.AnswerRepository;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;
    
    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;
    
    @PostMapping
    public ResponseEntity<Void> create(@LoginUser User loginUser, 
            @PathVariable long questionId,
            @Valid @RequestBody String contents) {
        Answer savedAnswer = qnaService.addAnswer(loginUser, questionId, contents);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + savedAnswer.generateUrl()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }
    
    @GetMapping("/{answerId}")
    public Answer show(@PathVariable long questionId, @PathVariable long answerId) {
        return answerRepository.findById(answerId)
                .orElseThrow(EntityNotFoundException::new);
    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long answerId) throws CannotDeleteException {
        qnaService.deleteAnswer(loginUser, answerId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
