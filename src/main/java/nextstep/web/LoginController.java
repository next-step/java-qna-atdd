package nextstep.web;

import nextstep.UnAuthenticationException;
import nextstep.domain.User;
import nextstep.security.HttpSessionUtils;
import nextstep.service.UserService;
import nextstep.dto.LoginInfoDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

import static nextstep.web.HomeController.*;

/**
 * Created by hspark on 24/11/2018.
 */
@Controller
public class LoginController {
	public static final String USER_LOGIN_VIEW_PATH = "/user/login";
	private final UserService userService;
	private final HttpServletRequest request;

	public LoginController(UserService userService, HttpServletRequest request) {
		this.userService = userService;
		this.request = request;
	}

	@GetMapping("/login")
	public String getLoginView() {
		return USER_LOGIN_VIEW_PATH;
	}

	@PostMapping("/login")
	public String login(LoginInfoDTO loginInfoDTO) throws UnAuthenticationException {
		User user = userService.login(loginInfoDTO.getUserId(), loginInfoDTO.getPassword());
		HttpSessionUtils.login(request.getSession(), user);
		return HOME_REDIRECT;
	}

	@GetMapping("/logout")
	public String login(){
		request.getSession().invalidate();
		return HOME_REDIRECT;
	}

}
