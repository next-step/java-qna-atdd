package nextstep.web;

import nextstep.CannotDeleteException;
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
public class QnaController {
    private static final Logger log = LoggerFactory.getLogger(QnaController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String form() {
        return "/qna/form";
    }

    @PostMapping
    public String create(@LoginUser User loginUser, Question question) {
        Question resultQuestion = qnaService.create(loginUser, question);
        return "redirect:/questions/" + resultQuestion.getId();
    }

    @GetMapping("/{id}")
    public String showQuestion(@PathVariable("id") long questionId, Model model) {
        Question question = qnaService.findById(questionId);
        model.addAttribute("question", question);
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable("id") long questionId, Model model) {
        Question question = qnaService.findById(questionId);
        question.hasAuthority(loginUser);

        model.addAttribute("question", question);
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, Question target) {
        Question resultQuestion = qnaService.update(loginUser, id, target);
        return "redirect:/questions/" + resultQuestion.getId();
    }


    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }

}