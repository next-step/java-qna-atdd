package nextstep.web;

import nextstep.domain.Question;
import nextstep.service.QnaService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class HomeController {
    @Resource(name="qnaService")
    private QnaService qnaService;

    @GetMapping("/")
    public String home(Model model, Pageable pageable) {
        List<Question> questions = qnaService.findAll(pageable);
        model.addAttribute("questions", questions);
        return "home";
    }
}
