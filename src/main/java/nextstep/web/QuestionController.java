package nextstep.web;

import lombok.RequiredArgsConstructor;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.exception.ObjectDeletedException;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import nextstep.web.dto.QuestionRequestDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QnaService qnaService;

    @GetMapping("/form")
    public String form() {
        return "/qna/form";
    }

    @PostMapping
    public String create(@LoginUser User loginUser, Question question) {
        qnaService.createQuestion(loginUser, question);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String show(Model model, @PathVariable long id) {
        Question question = qnaService.findQuestionById(id);
        model.addAttribute("question", question);
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findQuestionById(loginUser, id));
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, QuestionRequestDTO questionRequestDTO) {
        Question target = Question.of(questionRequestDTO);
        qnaService.updateQuestion(loginUser, id, target);
        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws ObjectDeletedException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }
}
