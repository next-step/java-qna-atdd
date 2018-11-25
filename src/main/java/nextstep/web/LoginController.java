package nextstep.web;

import javax.servlet.http.HttpSession;
import nextstep.UnAuthenticationException;
import nextstep.domain.User;
import nextstep.security.HttpSessionUtils;
import nextstep.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

	public static final String REDIRECT_TO_LIST = "redirect:/";

	@Autowired
	private UserService userService;

	@GetMapping("/login")
	public String login() {
		return "user/login";
	}

	@PostMapping("/login")
	public String login(HttpSession session, User user) throws UnAuthenticationException {
		User loginUser = userService.login(user.getUserId(), user.getPassword());
		session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, loginUser);
		return REDIRECT_TO_LIST;
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return REDIRECT_TO_LIST;
	}

	@ExceptionHandler(UnAuthenticationException.class)
	public String loginFail(UnAuthenticationException exception) {
		return "user/login_failed";
	}
}
