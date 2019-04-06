package nextstep.web;

import lombok.RequiredArgsConstructor;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final QnaService qnaService;
    
    @GetMapping("/")
    public String home(@LoginUser User loginUser,  Model model) {
        model.addAttribute("questions", qnaService.findAll());
        return "home";
    }
}
