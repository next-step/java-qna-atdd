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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    private final QnaService qnaService;

    public QuestionController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping("/form")
    public String form(@LoginUser User loginUser) {
        return "/qna/form";
    }

    @PostMapping("/")
    public String create(@LoginUser User loginUser, String title, String contents) {
        Question createdQuestion = qnaService.createQuestion(loginUser, new Question(title, contents));

        return "redirect:" + createdQuestion.generateUrl();
    }

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model) {
        Question question = qnaService.findById(id);

        model.addAttribute("question", question);
        model.addAttribute("answersSize", question.getAnswers().size());

        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {

        model.addAttribute("question", qnaService.findById(id));

        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id,
        String title, String contents) {

        qnaService.update(loginUser, id, new Question(title, contents));

        return "redirect:" + qnaService.findById(id).generateUrl();
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id)
        throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);

        return "redirect:/";
    }
}
