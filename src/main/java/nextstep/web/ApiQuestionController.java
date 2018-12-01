package nextstep.web;

import nextstep.UnAuthenticationException;
import nextstep.domain.Question;
import nextstep.domain.QuestionPost;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RequestMapping("/api/questions")
@RestController
public class ApiQuestionController {

    private final QnaService qnaService;

    public ApiQuestionController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping("/{questionId}")
    public Question show(@PathVariable Long questionId) {

        return qnaService.findNotDeletedQuestionById(questionId);
    }

    @PostMapping
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody Question question) throws UnAuthenticationException {

        final Question createdQuestion = qnaService.create(loginUser, question);

        return ResponseEntity.created(URI.create("/api/questions/" + createdQuestion.getId())).build();
    }

    @PutMapping("/{questionId}")
    public Question update(@LoginUser User loginUser, @PathVariable Long questionId,
                           @Valid @RequestBody QuestionPost updatedPost) throws UnAuthenticationException {


        return qnaService.update(loginUser, questionId, updatedPost);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{questionId}")
    public void delete(@LoginUser User loginUser, @PathVariable Long questionId) throws UnAuthenticationException {
        qnaService.deleteQuestion(loginUser, questionId);
    }

}
