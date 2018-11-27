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

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);
    public static final String ROUTE_QNA_FORM = "/qna/form";
    public static final String ROUTE_QNA_SHOW = "/qna/show";
    public static final String ROUTE_HOME = "redirect:/";
    public static final String ROUTE_QNA_UPDATE_FORM = "/qna/updateForm";
    public static final String ATTRIBUTE_QUESTION = "question";

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String form() {
        return ROUTE_QNA_FORM;
    }

    @GetMapping("/{id}")
    public String readDetail(@PathVariable long id, Model model) {
        model.addAttribute(ATTRIBUTE_QUESTION, qnaService.findById(id).get());
        return ROUTE_QNA_SHOW;
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);
        return ROUTE_HOME;
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute(ATTRIBUTE_QUESTION, qnaService.findById(id).get());
        return ROUTE_QNA_UPDATE_FORM;
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, Question target) {
        qnaService.update(loginUser, id, target);
        return ROUTE_HOME;
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return ROUTE_HOME;
    }
}
