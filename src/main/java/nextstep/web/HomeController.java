package nextstep.web;

import nextstep.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {
    private QnaService qnaService;

    @Autowired
    public HomeController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping("/")
    public String home(@PageableDefault Pageable pageable, Model model) {
        model.addAttribute("questions", qnaService.findAll(pageable));
        return "home";
    }
}
