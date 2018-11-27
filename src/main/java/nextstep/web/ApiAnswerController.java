package nextstep.web;

import nextstep.UnAuthenticationException;
import nextstep.domain.Answer;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/api/questions/{questionId}/answers")
@RestController
public class ApiAnswerController {

    private final QnaService qnaService;

    public ApiAnswerController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @PostMapping
    public Answer add(@LoginUser User loginUser, @PathVariable Long questionId,
                            @Valid @RequestBody Answer answer) throws UnAuthenticationException {

       return qnaService.addAnswer(loginUser, questionId, answer);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{answerId}")
    public void delete(@LoginUser User loginUser, @PathVariable Long questionId, @PathVariable Long answerId) throws UnAuthenticationException {
        qnaService.deleteAnswer(loginUser, questionId, answerId);
    }


}
