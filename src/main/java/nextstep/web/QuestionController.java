package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.QuestionNotFoundException;
import nextstep.UnAuthorizedException;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("")
    public String list(Model model) {
        Iterable<Question> questionIterable = qnaService.findAll();
        List<Question> questions = new ArrayList<>();
        questionIterable.iterator()
                .forEachRemaining(questions::add);

        log.debug("user size : {}", questions.size());
        model.addAttribute("question", questions);
        return "/qna/show";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);
        return "redirect:/questions";
    }

    @GetMapping("/form")
    public String form() {
        return "/qna/form";
    }

    @GetMapping("/{id}")
    public String showDetail(@PathVariable Long id, Model model) {
        Question question = qnaService.findById(id).orElseThrow(QuestionNotFoundException::new);
        model.addAttribute("question", question);
        return "/qna/show";
    }

    @PostMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, Question question) {
        qnaService.update(loginUser, id, question);
        return "redirect:/questions";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/questions";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        Question question = qnaService.findById(id).orElseThrow(QuestionNotFoundException::new);
        if(!question.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        model.addAttribute("question", question);
        return "/qna/updateForm";
    }
}
