package nextstep.web;

import java.util.List;
import javax.annotation.Resource;
import nextstep.domain.Question;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/")
    public String home(
        Model model,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "20") int size) {

        List<Question> questions = qnaService.findAll(PageRequest.of(page - 1, size));
        log.debug("question size : {}", questions.size());
        model.addAttribute("questions", questions);
        return "home";
    }
}
