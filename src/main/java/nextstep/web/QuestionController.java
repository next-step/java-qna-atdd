package nextstep.web;

import java.util.List;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnAService;
import nextstep.web.exception.ForbiddenException;
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
    public String create(@LoginUser User user, Question question, Model model) {
        Question result = qnAService.create(user, question);

        model.addAttribute("question", result);
        return "qna/show";
    }

    @GetMapping("{id}/form")
    public String updateForm(@LoginUser User user, @PathVariable Long id, Model model) {
        Question result = qnAService.findById(id);

        if(!result.isOwner(user)) {
            throw new ForbiddenException();
        }

        model.addAttribute("question", result);
        return "qna/updateForm";
    }

    @PatchMapping("{id}")
    public String update(@LoginUser User user, @PathVariable Long id, Question question, Model model) {
        Question result = qnAService.update(user, id, question);

        model.addAttribute("question", result);
        return "qna/show";
    }

    @DeleteMapping("{id}")
    public String delete(@LoginUser User user, @PathVariable Long id, Model model) {
        qnAService.deleteQuestion(user, id);

        List<Question> list = qnAService.findAll();

        model.addAttribute("questions", list);
        return "home";
    }
}
