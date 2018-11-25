package nextstep.web;

import nextstep.security.HttpSessionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/logout")
public class LogoutController {
    @GetMapping("")
    public String logout(HttpSession httpSession) {
        HttpSessionUtils.removeUserFromSession(httpSession);
        return "/";
    }
}
