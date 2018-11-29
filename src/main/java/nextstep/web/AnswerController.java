package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.AnswerService;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/questions")
public class AnswerController {
    private static final Logger log = LoggerFactory.getLogger(AnswerController.class);

    private final String REDIRECT_QUESTIONS = "redirect:/questions";

    @Resource(name = "answerService")
    private AnswerService answerService;

    @PostMapping("/{id}/answers")
    public String createAnswer(@LoginUser User loginUser, @PathVariable long id, String contents) {
        Answer answer = answerService.addAnswer(loginUser, id, contents);
        return REDIRECT_QUESTIONS+"/"+id;
    }

    @PutMapping("/{id}/answers/{answerId}")
    public String updateAnswer(@LoginUser User loginUser, @PathVariable long id, @PathVariable long answerId, String contents) {
        answerService.updateAnswer(loginUser, answerId, contents);
        return REDIRECT_QUESTIONS+"/"+id;
    }


    @DeleteMapping("/{id}/answers/{answerId}")
    public String deleteAnswer(@LoginUser User loginUser, @PathVariable long id, @PathVariable long answerId) throws CannotDeleteException {
        answerService.deleteAnswer(loginUser, answerId);
        return REDIRECT_QUESTIONS+"/"+id;
    }
}
