package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Optional;

@Controller
@RequestMapping("/questions")
public class QnaController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("{id}")
    public String detail(@PathVariable Long id, Model model) {
        Optional<Question> question = qnaService.findById(id);
        model.addAttribute("question", question.get());

        return "/qna/show";
    }

    @GetMapping("form")
    public String createForm(@LoginUser User loginUser) {
        if (loginUser != null) {
            return "/qna/form";
        }

        return "redirect:/";
    }

    @PostMapping
    public String create(@LoginUser User user, Question question) {
        qnaService.create(user, question);

        return "redirect:/";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable Long id, Model model) {
        try {
            model.addAttribute("question", qnaService.findById(id).get());

            return "/qna/updateForm";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @PutMapping("{id}")
    public String update(@LoginUser User user, @PathVariable Long id, Question question) {
        try {
            qnaService.update(user, id, question);

            return "redirect:/questions/" + id;
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @DeleteMapping("{id}")
    public String delete(@LoginUser User loginUser, @PathVariable Long id) {
        try {
            qnaService.deleteQuestion(loginUser, id);

            return "redirect:/";
        } catch (Exception ex) {
            return "redirect:/questions/" + id;
        }

    }
}
