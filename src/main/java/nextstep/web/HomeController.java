package nextstep.web;

import nextstep.domain.Question;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class HomeController {
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/")
    public String home(Model model) {
        List<Question> questions = StreamSupport
            .stream(qnaService.findAll().spliterator(), false)
            .collect(Collectors.toList());
        log.debug("question size : {}", questions.size());
        model.addAttribute("questions", questions);
        return "home";
    }
}
