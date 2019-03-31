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
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id).get());
        return "/qna/show";
    }

    @GetMapping("/form")
    public String form(@LoginUser User user) {
        return "/qna/form";
    }

    @PostMapping("")
    public String create(@LoginUser User user, String title, String contents) {
        qnaService.create(user, new Question(title, contents));
        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String deleteQuestion(@LoginUser User user, @PathVariable long id) {
        try {
            qnaService.deleteQuestion(user, id);
        } catch (CannotDeleteException e) {
            log.error("{}", e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        try {
            model.addAttribute("question", qnaService.findByIdAndUser(loginUser, id));
        } catch (NoSuchElementException e) {
            log.error("cannot find Question by id: {}, userId: {}", id, loginUser.getUserId());
            return "redirect:/";
        }
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String updateQuestion(@LoginUser User loginUser, @PathVariable long id, String title, String contents, Model model) {
        try {
            Question question = qnaService.findByIdAndUser(loginUser, id);
            question.setTitle(title);
            question.setContents(contents);

            qnaService.update(loginUser, id, question);
            model.addAttribute("question", qnaService.findById(id).get());
        } catch (NoSuchElementException e) {
            log.error("{}", e.getMessage());
            return "redirect:/";
        }
        return "/qna/show";
    }
}