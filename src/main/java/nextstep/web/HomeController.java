package nextstep.web;

import nextstep.domain.Question;
import nextstep.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

@Controller
public class HomeController {
    @Resource(name = "qnaService")
    QnaService qnaService;

    @GetMapping("/")
    public String home(Model model) {
        Iterable<Question> all = qnaService.findAll();
        model.addAttribute("questions", all);

        return "home";
    }


}
