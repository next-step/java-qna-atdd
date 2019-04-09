package nextstep.web;

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
    public String form(@LoginUser User loginuser) {
        return "qna/form";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Question result = qnaService.findById(id);
        model.addAttribute("question", result);
        return "qna/show";
    }

    @PostMapping
    public String create(@LoginUser User loginUser, Question question) {
        this.qnaService.create(loginUser, question);
        return "redirect:/";
    }

    @GetMapping("list")
    public String list(Model model, Pageable pageable) {
        List<Question> questions = qnaService.findAll(pageable);
        log.debug("questions size : {}", questions.size());
        model.addAttribute("questions", questions);
        return "/home";
    }

    @PutMapping("{id}")
    public String update(@PathVariable("id") long id, @LoginUser User loginUser, Question question, Model model) {
        model.addAttribute("question", qnaService.update(loginUser, id, question));
        return "redirect:/";
    }

    @DeleteMapping("{id}")
    public String delete(@PathVariable("id") long id, @LoginUser User loginUser) throws Exception {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }
}
