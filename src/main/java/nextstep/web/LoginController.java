package nextstep.web;

import com.sun.org.apache.xpath.internal.operations.Mod;
import nextstep.UnAuthenticationException;
import nextstep.domain.User;
import nextstep.security.HttpSessionUtils;
import nextstep.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {
    public static final String USER_LOGIN_FAILED = "/user/login_failed";
    public static final String USER_LOGIN = "/user/login";
    
    @Autowired
    UserService userService;

    @GetMapping("")
    public String loginForm() {
        return USER_LOGIN;
    }

    @PostMapping("")
    public String login(String userId, String password, HttpSession httpSession, Model model) {
        try {
            User login = userService.login(userId, password);
            HttpSessionUtils.setUserToSession(httpSession, login);

        } catch (UnAuthenticationException e) {
            e.printStackTrace();
            return USER_LOGIN_FAILED;
        }
        return "redirect:/";
    }
}
