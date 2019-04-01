package nextstep.web;

import nextstep.domain.Question;
import nextstep.service.QnaService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    private final QnaService qnaService;

    public HomeController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping("/")
    public String home(Model model, @PageableDefault Pageable pageable) {
        List<Question> questions = qnaService.findUsedAll(pageable);
        model.addAttribute("questions", questions);
        
        return "home";
    }
}
