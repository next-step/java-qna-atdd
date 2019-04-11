package nextstep.web;

import nextstep.domain.Question;
import nextstep.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private QnaService qnaService;

    @GetMapping("/")
    public String index(Model model) {
        List<Question> questions = qnaService.findQuestions();
        model.addAttribute("questions", questions);

        return "home";
    }
}
