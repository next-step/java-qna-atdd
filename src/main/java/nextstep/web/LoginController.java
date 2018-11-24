package nextstep.web;

import nextstep.UnAuthenticationException;
import nextstep.domain.User;
import nextstep.security.HttpSessionUtils;
import nextstep.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    UserService userService;

    @GetMapping("")
    public String login() {
        return "/user/login";
    }

    @PostMapping("")
    public String login(String userId, String password, HttpServletResponse response,
                        HttpSession httpSession) throws UnAuthenticationException {
        try {
            User user = userService.login(userId, password);
            HttpSessionUtils.setUserFormSession(httpSession, user);
            return "redirect:/users";
        } catch (UnAuthenticationException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "/user/login_failed";
        }
    }
}
