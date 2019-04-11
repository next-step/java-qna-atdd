package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping("/answers")
public class AnswerController {

    @Resource(name = "qnaService")
    private QnaService qnaService;


    @PostMapping("")
    public String add(@LoginUser User user, long questionId, String contents) {
        qnaService.addAnswer(user, questionId, contents);
        return "redirect:/questions";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteAnswer(loginUser, id);
        return "redirect:/questions";
    }
}
