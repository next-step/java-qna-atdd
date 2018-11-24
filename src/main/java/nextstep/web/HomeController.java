package nextstep.web;

import nextstep.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	public static final String HOME_PATH = "home";
	public static final String HOME_REDIRECT = "redirect:/";
	private final QnaService qnaService;

	public HomeController(QnaService qnaService) {
		this.qnaService = qnaService;
	}

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("questions", qnaService.findAll());
		return HOME_PATH;
	}
}
