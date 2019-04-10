package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.dto.QuestionDto;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    private QnaService qnaService;

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model) {
        Question question = qnaService.findQuestion(id);
        model.addAttribute("question", question);
        return "/qna/show";
    }

    @GetMapping("/form")
    public String form(@LoginUser User loginuser) {
        return "/qna/form";
    }

    @PostMapping("/")
    public String create(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);
        return "redirect:/";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        Question question = qnaService.findQuestion(id);
        model.addAttribute("question", question);
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, QuestionDto updateQuestionDto) throws CannotUpdateException {
        qnaService.updateQuestion(loginUser, id, updateQuestionDto);
        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }
}
