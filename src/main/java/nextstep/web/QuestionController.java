package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.QuestionNotFoundException;
import nextstep.UnAuthorizedException;
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
        List<Question> questions = qnaService.findAllQuestions();

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

    @PostMapping("{id}/answers")
    public String createAnswer(@LoginUser User loginUser, @PathVariable long id, Answer answer){
        qnaService.addAnswer(loginUser, id, answer.getContents());
        return "redirect:/questions";
    }

    @DeleteMapping("/answers/{answerId}")
    public String deleteAnswer(@LoginUser User loginUser, @PathVariable long answerId) throws CannotDeleteException {
        qnaService.deleteAnswer(loginUser, answerId);
        return "redirect:/questions";
    }

    @GetMapping("/answers/{id}/form")
    public String answerUpdateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        Answer answer = qnaService.findAnswerById(id);
        if(!answer.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        model.addAttribute("answer", answer);
        return "/qna/answerUpdateForm";
    }

    @PostMapping("/answers/{id}")
    public String updateAnswer(@LoginUser User loginUser, @PathVariable long id, Answer answer) {
        qnaService.updateAnswer(loginUser, id, answer.getContents());
        return "redirect:/questions";
    }
}
