package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.QuestionBody;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.AnswerService;
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

    private final String HOME_VIEW = "home";
    private final String REDIRECT_QUESTIONS = "redirect:/questions";
    private final String QNA_UPDATE_VIEW = "/qna/updateForm";
    private final String QNA_DETAIL_VIEW = "/qna/show";
    private final String QNA_INSERT_VIEW = "/qna/form";
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @Resource(name = "answerService")
    private AnswerService answerService;

    @GetMapping("/form")
    public String form() {
        return QNA_INSERT_VIEW;
    }

    @PostMapping("")
    public String createQuestion(@LoginUser User loginUser, QuestionBody target) {
        qnaService.createQuestion(loginUser, target);
        return REDIRECT_QUESTIONS;
    }

    @GetMapping("")
    public String list(Model model) {
        Iterable<Question> questions = qnaService.findAll();
        model.addAttribute("questions", questions);
        return HOME_VIEW;
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findByUserId(loginUser, id));
        return QNA_UPDATE_VIEW;
    }

    @PutMapping("/{id}")
    public String updateQuestion(@LoginUser User loginUser, @PathVariable long id, QuestionBody target) {
        qnaService.updateQuestion(loginUser, id, target);
        return REDIRECT_QUESTIONS;
    }

    @DeleteMapping("/{id}")
    public String deleteQuestion(@LoginUser User loginUser, @PathVariable long id, Model model) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return REDIRECT_QUESTIONS;
    }


    @GetMapping("/{id}")
    public String showQuestion(@PathVariable long id, Model model) {
        Question question = qnaService.findById(id).orElseThrow(EntityNotFoundException::new);
        model.addAttribute("question", question);
        model.addAttribute("answers", qnaService.findByQuestionIdAll(id));
        model.addAttribute("size", qnaService.findByQuestionIdAll(id).size());
        return QNA_DETAIL_VIEW;
    }
}
