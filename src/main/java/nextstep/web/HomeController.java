package nextstep.web;

import javax.servlet.http.HttpSession;
import nextstep.domain.User;
import nextstep.security.HttpSessionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {
        return "home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        User user = HttpSessionUtils.getUserFromSession(httpSession);

        if (user.isGuestUser()) {
            throw new IllegalAccessError();
        }

        httpSession.removeAttribute(HttpSessionUtils.USER_SESSION_KEY);

        return "redirect:/";
    }
}
