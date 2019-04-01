package nextstep.web;

import nextstep.domain.User;
import nextstep.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/login")
public class LoginController {
    public static final String USER_SESSION_KEY = "loginedUser";

    @Resource(name = "userService")
    private UserService userService;

    @GetMapping
    public String loginForm(Model model) {
        return "/user/login";
    }

    @PostMapping
    public String login(User user, HttpSession session) {
        try {
            User loginUser = userService.login(user.getUserId(), user.getPassword());
            session.setAttribute(USER_SESSION_KEY, loginUser);
            return "redirect:/";
        } catch (Exception ex) {
            return "/user/login_failed";
        }
    }
}
