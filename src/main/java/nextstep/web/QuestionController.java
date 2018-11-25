package nextstep.web;

import nextstep.UnAuthenticationException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/questions")
@Controller
public class QuestionController {

    private final QnaService qnaService;

    public QuestionController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping("/{questionId}")
    public String show(@PathVariable Long questionId, Model model) {

        final Question question = qnaService.findNotDeletedQuestionById(questionId);

        model.addAttribute("question", question);
        return "/qna/show";
    }

    @GetMapping("/form")
    public String form(@LoginUser User loginUser) {

        return "/qna/form";
    }

    @PostMapping
    public String create(@LoginUser User loginUser, Question question, Model model) throws UnAuthenticationException {

        final Question created = qnaService.create(loginUser, question);

        return "redirect:/questions/"+created.getId();
    }

    @GetMapping("/{questionId}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable Long questionId, Model model) {

        final Question question = qnaService.findNotDeletedQuestionById(questionId);

        model.addAttribute("question", question);

        return "/qna/updateForm";
    }

    @PutMapping("/{questionId}")
    public String update(@LoginUser User loginUser, @PathVariable Long questionId, Question updatedQuestion, Model model) throws UnAuthenticationException {

        qnaService.update(loginUser, questionId, updatedQuestion);

        return "redirect:/questions/"+questionId;
    }

    @DeleteMapping("/{questionId}")
    public String delete(@LoginUser User loginUser, @PathVariable Long questionId) throws UnAuthenticationException {

        qnaService.deleteQuestion(loginUser, questionId);

        return "redirect:/";
    }


}
