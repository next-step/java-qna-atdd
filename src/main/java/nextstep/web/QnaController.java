package nextstep.web;

import nextstep.UnAuthenticationException;
import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping("/questions")
public class QnaController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String createQuestionForm(@LoginUser User loginUser) {
        return "/qna/form";
    }

    @PostMapping("")
    public String createQuestion(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);
        return "redirect:/";
    }


    @GetMapping("/{id}/form")
    public String updateQuestionForm(@LoginUser User loginUser, @PathVariable Long id, Model model) {
        qnaService.findById(id);
        model.addAttribute(qnaService.findById(id).get());
        return "qna/updateForm";
    }

    @GetMapping("/{id}")
    public String showQuestion(@PathVariable Long id, Model model) {
        model.addAttribute("question", qnaService.findById(id).get());
        return "/qna/show";
    }

    @PutMapping("/{id}")
    public String updateQuestion(@LoginUser User loginUser, @PathVariable Long id, Question question) throws UnAuthenticationException {
        Question updateQuestion = qnaService.update(loginUser, id, question);
        return "redirect:" + updateQuestion.generateUrl();
    }

    @DeleteMapping("/{id}")
    public String deleteQuestion(@LoginUser User loginUser, @PathVariable long id) throws UnAuthenticationException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }


    @PostMapping("/{questionId}/answers")
    public String createAnswer(@LoginUser User loginuser, @PathVariable long questionId, String contents) throws UnAuthenticationException {
        Answer answer = qnaService.addAnswer(loginuser, questionId, contents);
        return  "redirect:/questions/"+ questionId;
    }


    @DeleteMapping("/{questionId}/answers/{answerId}")
    public String deleteAnswer(@LoginUser User loginuser, @PathVariable long questionId, @PathVariable long answerId) throws UnAuthenticationException {
        Answer answer = qnaService.deleteAnswer(loginuser,answerId);
        return  "redirect:/questions/" + questionId;
    }




}
