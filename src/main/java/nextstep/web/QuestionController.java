package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import nextstep.UnAuthenticationException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QnaService qnaService;

    @GetMapping("/form")
    public String form() {
        return "/questions/form";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);

        return "redirect:/questions/";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("user", qnaService.findById(id));
        return "/questions/form";
    }

    @GetMapping("/{id}")
    public String find(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("user", qnaService.findById(id));
        return "redirect:/qna/show";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, Question updateQuestion, Model model) throws UnAuthenticationException, CannotUpdateException {
        qnaService.update(loginUser, id, updateQuestion);
        model.addAttribute("user", qnaService.findById(id));
        return "redirect:/qna/show";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id, Question updateQuestion, Model model) throws UnAuthenticationException, CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/questions/";
    }
}
