package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnAService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    private final QnAService qnaService;

    public ApiAnswerController(QnAService qnaService) {
        this.qnaService = qnaService;
    }

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable Long questionId, @RequestBody Answer answer) {
        return null;
    }
}
