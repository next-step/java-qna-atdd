package nextstep.web;

import nextstep.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {
    @Autowired
    QnaService qnaService;

    @GetMapping("/")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("questions", qnaService.findAll(pageable));
        return "home";
    }
}
