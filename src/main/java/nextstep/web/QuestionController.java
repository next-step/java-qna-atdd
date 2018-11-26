package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.CannotUpdateException;
import nextstep.UnAuthenticationException;
import nextstep.domain.Question;
import nextstep.domain.User;
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
    public static final String QUESTIONS_FORM = "/questions/form";
    public static final String REDIRECT_QUESTIONS = "redirect:/questions/";
    public static final String QNA_UPDATE_FORM = "/qna/updateForm";
    public static final String QNA_SHOW = "/qna/show";
    
    @Autowired
    QnaService qnaService;

    @GetMapping("/form")
    public String form() {
        return QUESTIONS_FORM;
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);

        return REDIRECT_QUESTIONS +question.getId();
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id).get());
        return QNA_UPDATE_FORM;
    }

    @GetMapping("/{id}")
    public String find(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id).get());
        return QNA_SHOW;
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, Question updateQuestion, Model model) throws UnAuthenticationException, CannotUpdateException {
        qnaService.update(loginUser, id, updateQuestion);
        model.addAttribute("question", qnaService.findById(id).get());
        model.addAttribute("user", loginUser);
        return REDIRECT_QUESTIONS+id;
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id, Question updateQuestion, Model model) throws UnAuthenticationException, CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return REDIRECT_QUESTIONS;
    }


    @PostMapping("/{questionId}/answers")
    public String createAnswer(@LoginUser User loginUser,@PathVariable long questionId, String contents) {
        qnaService.addAnswer(loginUser, questionId, contents);
        return REDIRECT_QUESTIONS+questionId;
    }

    @DeleteMapping("/{questionId}/answers/{id}")
    public String deleteAnswer(@LoginUser User loginUser,@PathVariable long questionId) throws CannotDeleteException {
        qnaService.deleteAnswer(loginUser, questionId);
        return REDIRECT_QUESTIONS+questionId;
    }
}
