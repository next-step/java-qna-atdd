package nextstep.web;

import javax.servlet.http.HttpSession;
import nextstep.UnAuthenticationException;
import nextstep.domain.User;
import nextstep.security.HttpSessionUtils;
import nextstep.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

  @Autowired
  private UserService userService;

  @PostMapping("/login")
  public String login(HttpSession session, String userId, String password) throws UnAuthenticationException {
    User loginUser = userService.login(userId, password);
    session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, loginUser);
    return "redirect:/";
  }
}
