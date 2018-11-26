package nextstep.web;


import nextstep.CannotDeleteException;
import nextstep.UnAuthenticationException;
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
        model.addAttribute("question", qnaService.findById(id).get());
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id).get());
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

    @PostMapping("/{id}/delete")
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
}


