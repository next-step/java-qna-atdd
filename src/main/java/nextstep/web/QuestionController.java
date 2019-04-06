package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.show(id));
        return "/qna/show";
    }

    @GetMapping("/form")
    public String form(@LoginUser User user) {
        return "/qna/form";
    }

    @PostMapping("")
    public String create(@LoginUser User user, String title, String contents) {
        qnaService.create(user, new Question(title, contents));
        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String deleteQuestion(@LoginUser User user, @PathVariable long id) {
        qnaService.deleteQuestion(user, id);
        return "redirect:/";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findQuestion(id, loginUser));
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String updateQuestion(@LoginUser User loginUser, @PathVariable long id, Question updatedQuetion, Model model) {
        model.addAttribute("question", qnaService.update(loginUser, id, updatedQuetion));
        return "/qna/show";
    }
}