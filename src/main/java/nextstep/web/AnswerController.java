package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.User;
import nextstep.exception.CannotDeleteException;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping("/questions/{questionId}")
public class AnswerController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("/answers")
    public String createAnswer(@LoginUser User loginUser, @PathVariable long questionId, Answer answer) {
        qnaService.addAnswer(loginUser, questionId, answer.getContents());
        return "redirect:/questions" + questionId;
    }

    @DeleteMapping("/answers/{id}")
    public String deleteAnswer(@LoginUser User loginUser, @PathVariable long questionId, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteAnswer(loginUser, id);
        return "redirect:/questions" + questionId;
    }
}
