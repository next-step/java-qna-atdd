package nextstep.web;

import nextstep.domain.Question;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private QnaService qnaService;

    @GetMapping("/")
    public String home(Model model) {
        Iterable<Question> questions = qnaService.findAll();
        log.debug("questions size : {}");
        model.addAttribute("questions", questions);
        return "home";
    }
}