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
import javax.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    private final String REDIRECT_QUESTIONS = "redirect:/questions";
    private final String UPDATE_VIEW_QUESTIONS = "/qna/updateForm";
    private final String VIEW_QUESTIONS = "/qna/show";

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public String createQuestion(@LoginUser User user, Question question) {
        log.info("Question createQuestion method question : " + question + "  user : " + user);

        qnaService.createQuestion(user, question);
        return REDIRECT_QUESTIONS;
    }

    @GetMapping("/{id}")
    public Question readQuestion(@PathVariable Long id, Model model) {
        Question question = qnaService.findByIdAngLoginUser(id).orElseThrow(EntityNotFoundException::new);
        model.addAttribute("question", question);
        return question;
    }

    @PutMapping("/{id}")
    public String updateQuestion(@LoginUser User user, @PathVariable("id") long id, Question question) {
        log.info("Question updateQuestion method question : " + question + "  user : " + user + "  id : " + id);
        qnaService.updateQuestion(user, id ,question);
        return UPDATE_VIEW_QUESTIONS;
    }

    @DeleteMapping("/{id}")
    public String deleteQuestion(@LoginUser User user, @PathVariable("id") long id) throws CannotDeleteException {
        log.info("Question deleteQuestion method user : " + user + "  id : " + id);

        qnaService.deleteQuestion(user, id);
        return UPDATE_VIEW_QUESTIONS;
    }

    @PostMapping("/{id}/answer")
    public String createAnswer(@LoginUser User user, @PathVariable("id") long id, String contents, Model model) {
        log.info("Question createAnswer method contents : " + contents + "  user : " + user + "  id : " + id);
        qnaService.addAnswer(user, id, contents);
        Question question = qnaService.findByIdAngLoginUser(id).orElseThrow(EntityNotFoundException::new);

        model.addAttribute("question", question);
        return VIEW_QUESTIONS;
    }
}
