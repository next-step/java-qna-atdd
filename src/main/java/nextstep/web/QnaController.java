package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.NotFoundExeption;
import nextstep.UnAuthorizedException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping("/questions")
public class QnaController {
    private static final Logger log = LoggerFactory.getLogger(QnaController.class);
    @Resource(name = "qnaService")
    QnaService qnaService;

    @GetMapping("/form")
    public String showWriteQuestionForm(@LoginUser User loginUser) {
        return "qna/form";
    }

    @PostMapping
    public String createQuestion(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String showQuestion(@PathVariable Long id, Model model){
        model.addAttribute("question", qnaService.findContentById(id));
        return "qna/show";
    }

    @GetMapping("/{id}/form")
    public String showUpdateDetail(@PathVariable Long id, Model model){
        model.addAttribute("question", qnaService.findContentById(id));
        return "qna/updateForm";
    }

    @PostMapping("/{id}")
    public String updateQuestion(@LoginUser User loginUser, @PathVariable Long id, Question question){
        qnaService.update(loginUser, id, question);
        return "redirect:/questions/" + id;
    }

    @DeleteMapping("/{id}")
    public String deleteQuestion(@LoginUser User loginUser, @PathVariable Long id){
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/questions";
    }

    @PostMapping("/{id}/answers")
    public String addAnswer(@LoginUser User loginUser, @PathVariable Long id, String contents){
        qnaService.addAnswer(loginUser, id, contents);
        return "redirect:/questions/" + id;
    }

    @DeleteMapping("/{questionId}/answers/{id}")
    public String deleteAnswer(@LoginUser User loginUser, @PathVariable Long questionId, @PathVariable Long id){
        qnaService.deleteAnswer(loginUser, id);
        return "redirect:/questions/" + questionId;
    }

    @ExceptionHandler(CannotDeleteException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void cannotDeleteException() {
        log.debug("CannotDeleteException is happened!");
    }
}
