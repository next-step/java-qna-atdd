package nextstep.web;

import java.util.Optional;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QuestionService;
import nextstep.web.exception.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Optional<Question> optionalQuestion = questionService.findById(id);

        if(!optionalQuestion.isPresent()) {
            throw new NotFoundException();
        }

        model.addAttribute("question", optionalQuestion.get());
        return "qna/show";
    }

    @GetMapping("/form")
    public String form(@LoginUser User user) {
        return "qna/form";
    }

    @PostMapping("")
    public String create(@LoginUser User user, Question question, Model model) {
        Question result = questionService.create(user, question);

        model.addAttribute("question", result);
        return "qna/show";
    }
}
