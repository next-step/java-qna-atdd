package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.UnAuthenticationException;
import nextstep.domain.Answer;
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
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String form() { return "/qna/form"; }

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question question, Model model) {
        model.addAttribute("question", qnaService.create(loginUser, question));
        return "/qna/show";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id));
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id));
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, Question target, Model model) {
        model.addAttribute("question", qnaService.update(loginUser, id, target));
        return "/qna/show";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }

    @PostMapping("/{questionId}/answers")
    public String addAnswer(@LoginUser User loginUser, @PathVariable long questionId, Answer answer, Model model) {
        qnaService.addAnswer(loginUser, questionId, answer);
        model.addAttribute("quesion", qnaService.findById(questionId));
        return "/qna/show";
    }

    @DeleteMapping("/{questionId}/answers/{id}")
    public String deleteAnswer(@LoginUser User loginUser, @PathVariable long questionId,
                               @PathVariable long id, Model model) throws CannotDeleteException {
        qnaService.deleteAnswer(loginUser, id);
        model.addAttribute("question", qnaService.findById(questionId));
        return "/qna/show";
    }
}
