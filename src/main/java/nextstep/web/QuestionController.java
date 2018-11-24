package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QnaService qnaService;
    @GetMapping("")
    public String list(Model model) {
        Iterable<Question> questions = qnaService.findAll();
        log.debug("questions size : {}");
        model.addAttribute("questions", questions);
        return "qna/show";
    }
}
