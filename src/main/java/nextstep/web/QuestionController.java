package nextstep.web;

import nextstep.domain.User;
import nextstep.security.LoginUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @GetMapping("/form")
    public String form(@LoginUser User user) {
        return "/qna/form";
    }

}