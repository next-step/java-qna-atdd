package nextstep.web;

import nextstep.domain.Question;
import nextstep.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class HomeController {
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Resource(name = "questionService")
    private QuestionService questionService;

    @GetMapping("/")
    public String home(Model model) {
        List<Question> questions = questionService.findAll();
        log.debug("question size : {}", questions.size());
        model.addAttribute("questions", questions);
        return "home";
    }
}
