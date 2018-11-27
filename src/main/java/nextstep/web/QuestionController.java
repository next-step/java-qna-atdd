package nextstep.web;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import nextstep.CannotDeleteException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String form() {
        return "/qna/form";
    }

    @PostMapping
    public String create(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);
        return "redirect:/home";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id));
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id));
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, Question updatedQuestion) {
        qnaService.update(loginUser, id, updatedQuestion);
        return String.format("redirect:/qna/show/%d",  id);
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/home";
    }
}
