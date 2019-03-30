package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/questions")
public class QnaController {
    private QnaService qnaService;

    @Autowired
    public QnaController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping("/form")
    public String createForm(@LoginUser User loginUser) {
        return "/qna/form";
    }

    @PostMapping("/register")
    public String registerQuestion(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);
        return "redirect:/";
    }

    @GetMapping("/show/{id}")
    public String showQuestion(@PathVariable("id") long questionId, Model model) {
        Question question = qnaService.findById(questionId)
                .orElseThrow(EntityNotFoundException::new);
        model.addAttribute("question", question);

        return "/qna/show";
    }

    @GetMapping("/update/{id}/form")
    public String updateForm(@PathVariable("id") long questionId, @LoginUser User loginUser, Model model) {
        Question question = qnaService.findById(questionId)
                .filter(q -> q.isOwner(loginUser))
                .orElseThrow(EntityNotFoundException::new);
        model.addAttribute("question", question);

        return "/qna/updateForm";
    }

    @PostMapping("/update/{id}")
    public String updateQuestion(@PathVariable("id") long questionId, @LoginUser User loginUser, Question question) {
        qnaService.update(loginUser, questionId, question);
        return "redirect:/";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable("id") long questionId, @LoginUser User loginUser) {
        qnaService.deleteQuestion(loginUser, questionId);
        return "redirect:/";
    }
}
