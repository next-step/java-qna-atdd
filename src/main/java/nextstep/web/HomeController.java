package nextstep.web;

import java.util.Collection;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import nextstep.domain.Question;
import nextstep.service.QnaService;

@Controller
public class HomeController {
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    
    @Resource(name = "qnaService")
    private QnaService qnaService;
    
    @GetMapping("/")
    public String home(Model model) {
        Iterable<Question> questions = qnaService.findAll();
        log.debug("qna size : {}", ((Collection<Question>)questions).size());
        model.addAttribute("questions", questions);
        return "home";
    }
}
