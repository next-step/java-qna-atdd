package nextstep.web;

import nextstep.CannotDeleteException;
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
        Question question = qnaService.findById(id);
        model.addAttribute("question", question);

        return "/qna/show";
    }

    @GetMapping("/form")
    public String form(@LoginUser User user) {
        return "/qna/form";
    }

    @PostMapping
    public String create(@LoginUser User user, Question question) {
        Question createdQuestion = qnaService.create(user, question);
        return "redirect:" + createdQuestion.generateUrl();
    }


    @DeleteMapping("/{id}")
    public String delete(@LoginUser User user, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(user, id);
        return "redirect:/";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User user, @PathVariable long id, Model model) {
        Question question = qnaService.findByIdAndOwner(id, user);
        model.addAttribute("question", question);
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User user, @PathVariable long id, Question target) {
        Question update = qnaService.update(user, id, target);
        return "redirect:" + update.generateUrl();
    }
}