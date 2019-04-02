package nextstep.web;

import nextstep.domain.Question;
import nextstep.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class HomeController {

    @Resource(name = "qnaService")
    private QnaService qnaService;
    @GetMapping("/")
    public String home(Model model) {
        Iterable<Question> questionsIterable = qnaService.findAll();
        List<Question> questions = new ArrayList<>();
        questionsIterable.forEach(questions::add);
        model.addAttribute("questions",questions);
        return "home";
    }
}
