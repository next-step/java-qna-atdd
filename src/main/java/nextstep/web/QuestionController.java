package nextstep.web;


import nextstep.CannotDeleteException;
import nextstep.UnAuthenticationException;
import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String form() {
        return "/qna/form";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);
        return "redirect:/questions";
    }

    @GetMapping("")
    public String list(Model model, Pageable pageable) {
        List<Question> questions = qnaService.findAll(pageable);
        log.debug("question size : {}", questions.size());
        model.addAttribute("questions", questions);
        return "/home";
    }

    @GetMapping("/{id}/detail")
    public String showDetail(@PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findByQuestionId(id));
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findByQuestionId(id));
        return "qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, Question updateQuestion) {
        try {
            qnaService.update(loginUser, id, updateQuestion);
        } catch (UnAuthenticationException e) {
            return "/home";
        }
        return "redirect:/questions";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) {
        try {
            qnaService.deleteQuestion(loginUser, id);
        } catch (CannotDeleteException e) {
            log.debug("삭제 실패하였습니다.");
            e.printStackTrace();
        } catch (UnAuthenticationException e) {
            log.debug("유저정보가 올바르지 않습니다.");
        } finally {
            return "/home";
        }
    }

    @PostMapping("/{id}/answers")
    public String createAnswer(@LoginUser User loginUser, @PathVariable long id, String answer) {
        qnaService.addAnswer(loginUser, id, answer);
        return "redirect:/questions";

    }

    @GetMapping("/{id}/answers/form")
    public String updateAnswerForm(@PathVariable long questionId, Model model) {
        model.addAttribute("question", qnaService.findByQuestionId(questionId));
        return "/qna/answerUpdateForm";
    }

    //답변수정
    @PutMapping("/answers/{id}")
    public String updateAnswer(@LoginUser User loginUser, @PathVariable long id, String updateContents) {
        qnaService.updateAnswer(loginUser, id, updateContents);
        return "redirect:/questions";

    }

    //답변삭제
    @DeleteMapping("/answers/{id}")
    public String deleteAnswer(@LoginUser User loginUser, @PathVariable long id) {
        try {
            qnaService.deleteAnswer(loginUser, id);
        } catch (CannotDeleteException e) {
            log.debug("삭제 실패하였습니다.");
            e.printStackTrace();
        } catch (UnAuthenticationException e) {
            log.debug("유저정보가 올바르지 않습니다.");
        } finally {
            return "/home";
        }
    }


}

