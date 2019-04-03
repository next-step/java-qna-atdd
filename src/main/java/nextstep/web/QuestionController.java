package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnAService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private final QnAService qnAService;

    public QuestionController(QnAService qnAService) {
        this.qnAService = qnAService;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Question result = qnAService.findById(id);

        model.addAttribute("question", result);
        return "qna/show";
    }

    @GetMapping("/form")
    public String form(@LoginUser User user) {
        return "qna/form";
    }

    @PostMapping("")
    public String create(@LoginUser User user, Question question) {
        Question result = qnAService.create(user, question);

        return "redirect:/questions/" + result.getId();
    }

    @GetMapping("{id}/form")
    public String updateForm(@LoginUser User user, @PathVariable Long id, Model model) {
        Question result = qnAService.findByOwner(user, id);

        model.addAttribute("question", result);
        return "qna/updateForm";
    }

    @PatchMapping("{id}")
    public String update(@LoginUser User user, @PathVariable Long id, Question question) {
        Question result = qnAService.update(user, id, question);

        return "redirect:/questions/" + result.getId();
    }

    @DeleteMapping("{id}")
    public String delete(@LoginUser User user, @PathVariable Long id) {
        qnAService.deleteQuestion(user, id);

        return "redirect:/home";
    }
}
