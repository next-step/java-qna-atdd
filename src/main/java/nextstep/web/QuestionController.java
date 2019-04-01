package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private final QnaService qnaService;

    public QuestionController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Question question = qnaService.findById(id).get();
        model.addAttribute("question", question);

        return "/qna/show";
    }

    @GetMapping("/form")
    public String form(@LoginUser User user) {
        return "/qna/form";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User user, @PathVariable Long id, Question target) {
        Question update = qnaService.update(user, id, target);
        return "redirect:" + update.generateUrl();
    }
}